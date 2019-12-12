import React, { Component } from 'react';
import './Login.css';
import { Link } from 'react-router-dom';

import {
  INCORRECT_USERNAME_OR_PASSWORD,
  ENTER_USERNAME_OR_EMAIL,
  ENTER_PASSWORD,
  BANNED_OR_DELETED_MESSAGE
} from '../util/messageConstants';
import { ACCESS_TOKEN } from '../util/constants';
import { login } from '../services/userService';
import { errorNotification } from '../util/notifications';

import { Form, Input, Button, Icon } from 'antd';

const FormItem = Form.Item;

class Login extends Component {
  render() {
    const AntWrappedLoginForm = Form.create()(LoginForm)
    return (
      <div className="login-container">
        <h1 className="page-title">Login</h1>
        <div className="login-content">
          <AntWrappedLoginForm onLogin={this.props.onLogin} />
        </div>
      </div>
    );
  }
}

class LoginForm extends Component {
  constructor(props) {
    super(props);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit(event) {
    event.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const loginRequest = Object.assign({}, values);

        login(loginRequest)
          .then(response => {
            localStorage.setItem(ACCESS_TOKEN, response.accessToken);
            this.props.onLogin();
          }).catch(error => {
            let message = '';
            if (error.status === 401) {
              if (error.message === 'Bad credentials') {
                message = INCORRECT_USERNAME_OR_PASSWORD;
              } else {
                message = BANNED_OR_DELETED_MESSAGE;
              }
            }
            errorNotification(error, message);
          });
      }
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    return (
      <Form onSubmit={this.handleSubmit} className="login-form">
        <FormItem>
          {getFieldDecorator('usernameOrEmail', {
            rules: [{ required: true, message: ENTER_USERNAME_OR_EMAIL }],
          })(
            <Input
              className='login-form-item'
              prefix={<Icon type="user" />}
              size="large"
              name="usernameOrEmail"
              placeholder="Username or Email" />
          )}
        </FormItem>
        <FormItem className='login-form-item'>
          {getFieldDecorator('password', {
            rules: [{ required: true, message: ENTER_PASSWORD }],
          })(
            <Input
              className='login-form-item'
              prefix={<Icon type="lock" />}
              size="large"
              name="password"
              type="password"
              placeholder="Password" />
          )}
        </FormItem>
        <FormItem>
          <Button type="primary" htmlType="submit" size="large" className="login-form-button">Login</Button>
          Or <Link to="/signup">register now!</Link>
        </FormItem>
      </Form>
    );
  }
}

export default Login;