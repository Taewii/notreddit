import React, { Component } from 'react';
import { checkSubredditAvailability, createSubreddit } from '../util/APIUtils';
import './SubredditCreate.css';
import { SUBREDDIT_MIN_LENGTH } from '../util/constants';

import { Form, Input, Button, notification, Icon } from 'antd';
const FormItem = Form.Item;

class SubredditCreate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: {
        value: ''
      },
    }

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.validateSubredditAvailability = this.validateSubredditAvailability.bind(this);
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
      name: this.state.name.value,
    };

    createSubreddit(signupRequest)
      .then(response => {
        notification.success({
          message: 'notreddit',
          description: "Subreddit successfully created.",
        });
        this.props.history.push("/");
      }).catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      });
  }

  isFormInvalid() {
    return !this.state.name.validateStatus === 'success';
  }

  render() {
    return (
      <div className="subreddit-container">
        <h1 className="page-title">Create Subreddit</h1>
        <div className="signup-content">
          <Form onSubmit={this.handleSubmit} className="subreddit-form">
            <FormItem label="Name"
              hasFeedback
              validateStatus={this.state.name.validateStatus}
              help={this.state.name.errorMsg}>
              <Input
                prefix={<Icon type="smile" />}
                size="large"
                name="name"
                autoComplete="off"
                placeholder="A unique subreddit name"
                value={this.state.name.value}
                onBlur={this.validateSubredditAvailability}
                onChange={(event) => this.handleInputChange(event, this.validateName)} />
            </FormItem>
            <FormItem>
              <Button type="primary"
                htmlType="submit"
                size="large"
                className="subreddit-form-button"
                disabled={this.isFormInvalid()}>Create Subreddit</Button>
            </FormItem>
          </Form>
        </div>
      </div>
    );
  }

  // Validation Functions
  validateName = (name) => {
    if (name.length < SUBREDDIT_MIN_LENGTH) {
      return {
        validateStatus: 'error',
        errorMsg: `Subreddit name is too short (Minimum ${SUBREDDIT_MIN_LENGTH} characters needed.)`
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }

  validateSubredditAvailability() {
    const nameValue = this.state.name.value;
    const nameValidation = this.validateName(nameValue);

    if (nameValidation.validateStatus === 'error') {
      this.setState({
        username: {
          value: nameValue,
          ...nameValidation
        }
      });
      return;
    }

    this.setState({
      name: {
        value: nameValue,
        validateStatus: 'validating',
        errorMsg: null
      }
    });

    checkSubredditAvailability(nameValue)
      .then(response => {
        if (response.available) {
          this.setState({
            username: {
              value: nameValue,
              validateStatus: 'success',
              errorMsg: null
            }
          });
        } else {
          this.setState({
            username: {
              value: nameValue,
              validateStatus: 'error',
              errorMsg: 'This username is already taken'
            }
          });
        }
      }).catch(error => {
        this.setState({
          username: {
            value: nameValue,
            validateStatus: 'success',
            errorMsg: null
          }
        });
      });
  }
}

export default SubredditCreate;