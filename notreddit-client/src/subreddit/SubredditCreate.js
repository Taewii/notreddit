import React, { Component } from 'react';
import './SubredditCreate.css';

import { SUBREDDIT_MIN_LENGTH } from '../util/constants';
import { checkSubredditAvailability, createSubreddit } from '../services/subredditService';
import { successNotification, errorNotification } from '../util/notifications';

import { Form, Input, Button, Icon } from 'antd';
const FormItem = Form.Item;

class SubredditCreate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: {
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
      title: this.state.title.value,
    };

    createSubreddit(signupRequest)
      .then(res => {
        if (res.success) {
          successNotification(res.message);
        } else {
          errorNotification(null, res.message)
        }
        this.props.history.push("/");
      }).catch(error => errorNotification(error));
  }

  isFormInvalid() {
    return this.state.title.validateStatus !== 'success';
  }

  render() {
    return (
      <div className="subreddit-container">
        <h1 className="page-title">Create Subreddit</h1>
        <div className="signup-content">
          <Form onSubmit={this.handleSubmit} className="subreddit-form">
            <FormItem label="Name"
              hasFeedback
              validateStatus={this.state.title.validateStatus}
              help={this.state.title.errorMsg}>
              <Input
                prefix={<Icon type="smile" />}
                size="large"
                name="title"
                autoComplete="off"
                placeholder="A unique subreddit name"
                value={this.state.title.value}
                onBlur={this.validateSubredditAvailability}
                onChange={(event) => this.handleInputChange(event, this.validateTitle)} />
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
  validateTitle = (title) => {
    if (title.length < SUBREDDIT_MIN_LENGTH) {
      return {
        validateStatus: 'error',
        errorMsg: `Subreddit title is too short (Minimum ${SUBREDDIT_MIN_LENGTH} characters needed.)`
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }

  validateSubredditAvailability() {
    const titleValue = this.state.title.value;
    const titleValidation = this.validateTitle(titleValue);

    if (titleValidation.validateStatus === 'error') {
      this.setState({
        title: {
          value: titleValue,
          ...titleValidation
        }
      });
      return;
    }

    this.setState({
      title: {
        value: titleValue,
        validateStatus: 'validating',
        errorMsg: null
      }
    });

    checkSubredditAvailability(titleValue)
      .then(response => {
        if (response.available) {
          this.setState({
            title: {
              value: titleValue,
              validateStatus: 'success',
              errorMsg: null
            }
          });
        } else {
          this.setState({
            title: {
              value: titleValue,
              validateStatus: 'error',
              errorMsg: 'This subreddit is already taken.'
            }
          });
        }
      }).catch(error => {
        this.setState({
          title: {
            value: titleValue,
            validateStatus: 'success',
            errorMsg: null
          }
        });
      });
  }
}

export default SubredditCreate;