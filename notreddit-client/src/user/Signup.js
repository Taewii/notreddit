import React, { Component } from 'react';
import './Signup.css';

import { Link } from 'react-router-dom';
import { Form, Input, Button } from 'antd';

import {
  USERNAME_MIN_LENGTH,
  PASSWORD_MIN_LENGTH
} from '../util/constants.js';

import {
  signup,
  checkUsernameAvailability,
  checkEmailAvailability
} from '../services/userService';
import { successNotification, errorNotification } from '../util/notifications';

const FormItem = Form.Item;

class Signup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: {
        value: ''
      },
      password: {
        value: ''
      },
      confirmPassword: {
        value: ''
      },
      email: {
        value: ''
      }
    }

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.validateUsernameAvailability = this.validateUsernameAvailability.bind(this);
    this.validateEmailAvailability = this.validateEmailAvailability.bind(this);
    this.isFormInvalid = this.isFormInvalid.bind(this);
  }

  handleInputChange(event, validationFun) {
    const target = event.target;
    const inputName = target.name;
    const inputValue = target.value;

    this.setState({
      [inputName]: {
        value: inputValue,
        ...validationFun(inputValue)
      }
    });
  }

  handleSubmit(event) {
    event.preventDefault();

    const signupRequest = {
      email: this.state.email.value,
      username: this.state.username.value,
      password: this.state.password.value,
      confirmPassword: this.state.confirmPassword.value,
    };

    signup(signupRequest)
      .then(res => {
        successNotification('Thank you! You\'re successfully registered. Please Login to continue!')
        this.props.history.push("/login");
      }).catch(error => errorNotification(error));
  }

  isFormInvalid() {
    return !(this.state.username.validateStatus === 'success' &&
      this.state.email.validateStatus === 'success' &&
      this.state.password.validateStatus === 'success' &&
      this.state.confirmPassword.validateStatus === 'success'
    );
  }

  render() {
    const { username, password, confirmPassword, email } = this.state;

    return (
      <div className="signup-container">
        <h1 className="page-title">Sign Up</h1>
        <div className="signup-content">
          <Form onSubmit={this.handleSubmit} className="signup-form">
            <FormItem label="Username"
              hasFeedback
              validateStatus={username.validateStatus}
              help={username.errorMsg}>
              <Input
                size="large"
                name="username"
                autoComplete="off"
                placeholder="A unique username"
                value={username.value}
                onBlur={this.validateUsernameAvailability}
                onChange={(event) => this.handleInputChange(event, this.validateUsername)} />
            </FormItem>
            <FormItem
              label="Password"
              validateStatus={password.validateStatus}
              help={password.errorMsg}>
              <Input
                size="large"
                name="password"
                type="password"
                autoComplete="off"
                placeholder="A password with minimum of 6 characters"
                value={password.value}
                onChange={(event) => this.handleInputChange(event, this.validatePassword)} />
            </FormItem>
            <FormItem
              label="Confirm Password"
              validateStatus={confirmPassword.validateStatus}
              help={confirmPassword.errorMsg}>
              <Input
                size="large"
                name="confirmPassword"
                type="password"
                autoComplete="off"
                placeholder="Please re-type your password"
                value={confirmPassword.value}
                onChange={(event) => this.handleInputChange(event, this.validateConfirmPassword)} />
            </FormItem>
            <FormItem
              label="Email"
              hasFeedback
              validateStatus={email.validateStatus}
              help={email.errorMsg}>
              <Input
                size="large"
                name="email"
                type="email"
                autoComplete="off"
                placeholder="Your email"
                value={email.value}
                onBlur={this.validateEmailAvailability}
                onChange={(event) => this.handleInputChange(event, this.validateEmail)} />
            </FormItem>
            <FormItem>
              <Button type="primary"
                htmlType="submit"
                size="large"
                className="signup-form-button"
                disabled={this.isFormInvalid()}>Sign up</Button>
              Already registered? <Link to="/login">Login now!</Link>
            </FormItem>
          </Form>
        </div>
      </div>
    );
  }

  // Validation Functions
  validateEmail = (email) => {
    if (!email) {
      return {
        validateStatus: 'error',
        errorMsg: 'Email may not be empty'
      }
    }

    const EMAIL_REGEX = RegExp('[^@ ]+@[^@ ]+\\.[^@ ]+');
    if (!EMAIL_REGEX.test(email)) {
      return {
        validateStatus: 'error',
        errorMsg: 'Email not valid'
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }

  validateUsername = (username) => {
    if (username.length < USERNAME_MIN_LENGTH) {
      return {
        validateStatus: 'error',
        errorMsg: `Username is too short (Minimum ${USERNAME_MIN_LENGTH} characters needed.)`
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }

  validateUsernameAvailability() {
    // First check for client side errors in username
    const usernameValue = this.state.username.value;
    const usernameValidation = this.validateUsername(usernameValue);

    if (usernameValidation.validateStatus === 'error') {
      this.setState({
        username: {
          value: usernameValue,
          ...usernameValidation
        }
      });
      return;
    }

    this.setState({
      username: {
        value: usernameValue,
        validateStatus: 'validating',
        errorMsg: null
      }
    });

    checkUsernameAvailability(usernameValue)
      .then(response => {
        if (response.available) {
          this.setState({
            username: {
              value: usernameValue,
              validateStatus: 'success',
              errorMsg: null
            }
          });
        } else {
          this.setState({
            username: {
              value: usernameValue,
              validateStatus: 'error',
              errorMsg: 'This username is already taken'
            }
          });
        }
      }).catch(error => {
        // Marking validateStatus as success, Form will be recchecked at server
        this.setState({
          username: {
            value: usernameValue,
            validateStatus: 'success',
            errorMsg: null
          }
        });
      });
  }

  validateEmailAvailability() {
    // First check for client side errors in email
    const emailValue = this.state.email.value;
    const emailValidation = this.validateEmail(emailValue);

    if (emailValidation.validateStatus === 'error') {
      this.setState({
        email: {
          value: emailValue,
          ...emailValidation
        }
      });
      return;
    }

    this.setState({
      email: {
        value: emailValue,
        validateStatus: 'validating',
        errorMsg: null
      }
    });

    checkEmailAvailability(emailValue)
      .then(response => {
        if (response.available) {
          this.setState({
            email: {
              value: emailValue,
              validateStatus: 'success',
              errorMsg: null
            }
          });
        } else {
          this.setState({
            email: {
              value: emailValue,
              validateStatus: 'error',
              errorMsg: 'This email is already in use'
            }
          });
        }
      }).catch(error => {
        // Marking validateStatus as success, Form will be recchecked at server
        this.setState({
          email: {
            value: emailValue,
            validateStatus: 'success',
            errorMsg: null
          }
        });
      });
  }

  validatePassword = (password) => {
    if (password.length < PASSWORD_MIN_LENGTH) {
      return {
        validateStatus: 'error',
        errorMsg: `Password is too short (Minimum ${PASSWORD_MIN_LENGTH} characters needed.)`
      }
    }

    return {
      validateStatus: 'success',
      errorMsg: null,
    };
  }

  validateConfirmPassword = (confirmPassword) => {
    const password = this.state.password.value;
    if (password.length && password !== confirmPassword) {
      return {
        validateStatus: 'error',
        errorMsg: 'Passwords don\'t match'
      }
    }

    return {
      validateStatus: 'success',
      errorMsg: null,
    }
  }
}

export default Signup;