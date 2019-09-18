import React, { Component } from 'react';
import './CreatePost.css';
import { Form, Input, Button, notification, Select, Upload, Icon } from 'antd';

import { TITLE_MIN_LENGTH } from '../util/constants.js';
import { getAllSubreddits } from '../services/subredditService';
import { create } from '../services/postService';

const { Dragger } = Upload;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;

class CreatePost extends Component {
  constructor(props) {
    super(props);
    this.options = [];
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

    getAllSubreddits()
      .then(res => {
        res.forEach(subreddit => {
          this.options.push(<Option value={subreddit.toLowerCase()}>{subreddit}</Option>)
        })
      })

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.isFormInvalid = this.isFormInvalid.bind(this);
    this.handleSelectionChange = this.handleSelectionChange.bind(this);
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

    const data = new FormData()
    data.append('title', this.state.title.value);
    data.append('url', this.state.url.value);
    data.append('content', this.state.content.value);
    data.append('subreddit', this.state.subreddit.value);
    data.append('file', this.state.file);

    create(data)
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

  handleSelectionChange(selectedValue) {
    this.setState({
      subreddit: {
        value: selectedValue
      }
    })
  }

  handleFileUpload = ({ file, onSuccess, onError }) => {
    const sizeInMb = file.size / 1024 / 1024

    // Simulating Ant-Design action responses with setTimeout.
    if (sizeInMb > 10) {
      setTimeout(() => {
        onError("file too big");
      }, 0);
      return
    }

    this.setState({ file: file })
    setTimeout(() => {
      onSuccess("ok");
    }, 0);
  };


  isFormInvalid() {
    return !(this.state.title.validateStatus === 'success' &&
      this.state.url.validateStatus === 'success'
    );
  }

  render() {
    return (
      <div className="post-container">
        <h1 className="page-title">Create a Post</h1>
        <div className="post-content">
          <Form onSubmit={this.handleSubmit} className="post-form">
            <FormItem label="Title"
              hasFeedback
              required
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
              required
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
            <FormItem label="Content">
              <TextArea
                name="content"
                autosize={{ minRows: 6 }}
                placeholder="Content"
                onChange={(event) => this.handleInputChange(event, () => void 0)} />
            </FormItem>
            <FormItem
              label="Subreddit"
              required>
              <Select showSearch
                placeholder="Select a subreddit"
                optionFilterProp="children"
                onChange={this.handleSelectionChange}
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }>
                {this.options}
              </Select>
            </FormItem>
            <FormItem label="File/Image">
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
      validateStatus: 'success',
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
      validateStatus: 'success',
      errorMsg: null
    }
  }
}

export default CreatePost;