import React, { Component } from 'react';
import './CreatePost.css';
import { Form, Input, Button, notification, Select, Upload, Icon } from 'antd';

import { TITLE_MIN_LENGTH } from '../util/constants.js';
import { getAllSubreddits } from '../services/subredditService';

const { Dragger } = Upload;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;

class CreatePost extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: {
        value: ''
      },
      url: {
        value: ''
      },
      subreddit: {
        value: ''
      },
      content: {
        value: ''
      },
      file: {
        value: ''
      }
    }

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
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

    const postRequest = {
      title: this.state.title.value,
      content: this.state.content.value,
    };

    fetch(postRequest)
      .then(response => {
        notification.success({
          message: 'notreddit',
          description: "Post successfully created.",
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
    return !(this.state.title.validateStatus === 'success' &&
      this.state.email.validateStatus === 'success' &&
      this.state.content.validateStatus === 'success' &&
      this.state.confirmPassword.validateStatus === 'success'
    );
  }

  handleFileUpload = ({ file, onSuccess, onError }) => {
    this.setState({ file: file })
    const sizeInMb = file.size / 1024 / 102

    if (sizeInMb > 10) {
      setTimeout(() => {
        onError("file too big");
      }, 0);
      return;
    }

    //TODO: MOVE ALL THIS SHIT ELSEWARE
    const data = new FormData()
    data.append('file', file)

    fetch('http://localhost:8000/api/role/idk', {
      method: 'POST', // *GET, POST, PUT, DELETE, etc.     
      body: data, // body data type must match "Content-Type" header
    })

    setTimeout(() => {
      onSuccess("ok");
    }, 0);
  };

  render() {
    const options = [];

    getAllSubreddits()
      .then(res => {
        res.forEach(subreddit => {
          options.push(<Option value={subreddit.toLowerCase()}>{subreddit}</Option>)
        })
      })

    return (
      <div className="post-container">
        <h1 className="page-title">Create a Post</h1>
        <div className="post-content">
          <Form onSubmit={this.handleSubmit} className="post-form">
            <FormItem label="Title"
              hasFeedback
              validateStatus={this.state.title.validateStatus}
              help={this.state.title.errorMsg}>
              <Input
                size="large"
                name="title"
                autoComplete="off"
                placeholder="Post Title"
                value={this.state.title.value}
                onChange={(event) => this.handleInputChange(event, this.validateTitle)} />
            </FormItem>
            <FormItem label="url"
              hasFeedback
              validateStatus={this.state.url.validateStatus}
              help={this.state.url.errorMsg}>
              <Input
                size="large"
                name="url"
                autoComplete="off"
                placeholder="URL"
                value={this.state.url.value}
                onChange={(event) => this.handleInputChange(event, this.validateUrl)} />
            </FormItem>
            <FormItem
              label="Content"
              validateStatus={this.state.content.validateStatus}
              help={this.state.content.errorMsg}>
              <TextArea
                name="content"
                autosize={{ minRows: 6 }}
                placeholder="Content"
                value={this.state.content.value} />
            </FormItem>
            <FormItem
              label="Subreddit">
              <Select showSearch
                placeholder="Select a subreddit"
                optionFilterProp="children"
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }>
                {options}
              </Select>
            </FormItem>
            <FormItem
              label="File/Image">
              <Dragger
                name="file"
                customRequest={this.handleFileUpload}>
                <p className="ant-upload-drag-icon">
                  <Icon type="inbox" />
                </p>
                <p className="ant-upload-text">Click or drag file to this area to upload</p>
                <p className="ant-upload-text">10MB size limit</p>
              </Dragger>,
            </FormItem>
            <FormItem>
              <Button type="primary"
                htmlType="submit"
                size="large"
                className="post-form-button"
                disabled={this.isFormInvalid()}>Create</Button>
            </FormItem>
          </Form>
        </div>
      </div>
    );
  }

  validateTitle = (title) => {
    if (title.length < TITLE_MIN_LENGTH) {
      return {
        validateStatus: 'error',
        errorMsg: `Title is too short (Minimum ${TITLE_MIN_LENGTH} characters needed.)`
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }

  validateUrl = (url) => {
    const URL_REGEX = RegExp(/(http|https):\/\/.+/gm);
    if (!URL_REGEX.test(url)) {
      return {
        validateStatus: 'error',
        errorMsg: 'URL not valid'
      }
    }

    return {
      validateStatus: null,
      errorMsg: null
    }
  }
}

export default CreatePost;