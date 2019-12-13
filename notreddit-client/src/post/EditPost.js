import React, { Component } from 'react';
import './CreatePost.css';
import { Form, Input, Button, Select, Upload, Icon } from 'antd';

import { INVALID_URL, FILE_TOO_BIG } from '../util/messageConstants';
import { successNotification, errorNotification } from '../util/notifications';
import { TITLE_MIN_LENGTH } from '../util/constants.js';
import { getAllSubreddits } from '../services/subredditService';
import { edit, findByIdForEditing } from '../services/postService';

const { Dragger } = Upload;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;

class EditPost extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      id: this.props.match.params.id,
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
        value: null
      },
      options: [],
      imageUrl: '',
      hasUploadedFile: false
    };

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.isFormInvalid = this.isFormInvalid.bind(this);
    this.handleSelectionChange = this.handleSelectionChange.bind(this);
    this.handleFileRemove = this.handleFileRemove.bind(this);
  }

  componentDidMount() {
    this.loadData();
  }

  loadData() {
    Promise.all([findByIdForEditing(this.state.id), getAllSubreddits()])
      .then(res => {
        const postDetails = res[0];
        const options = [];
        let hasUploadedFile = false;
        let imageUrl = '';

        if (postDetails.fileUrl && postDetails.fileUrl.indexOf('dropbox') > -1) {
          imageUrl = postDetails.fileUrl;
          postDetails.fileUrl = '';
          hasUploadedFile = true;
        }

        res[1].forEach(subreddit => {
          options.push(<Option value={subreddit} key={subreddit}>{subreddit}</Option>)
        });

        this.setState({
          title: {
            value: postDetails.title,
            validationStatus: 'success'
          },
          url: {
            value: postDetails.fileUrl || '',
          },
          subreddit: {
            value: postDetails.subredditTitle,
            validationStatus: 'success'
          },
          content: {
            value: postDetails.content
          },
          options,
          imageUrl,
          hasUploadedFile
        });
      })
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
    this.setState({ loading: true });

    const data = new FormData();
    data.append('postId', this.state.id);
    data.append('title', this.state.title.value);
    data.append('url', this.state.url.value);
    data.append('content', this.state.content.value);
    data.append('subreddit', this.state.subreddit.value);
    data.append('hasUploadedFile', this.state.hasUploadedFile);

    if (this.state.file.value !== null) {
      data.append('file', this.state.file.value);
    }

    edit(data)
      .then(res => {
        if (res.success) {
          successNotification(res.message);
        } else {
          errorNotification(null, res.message);
        }
        this.props.history.push('/post/' + this.state.id);
      }).catch(error => errorNotification(error));
  }

  handleSelectionChange(selectedValue) {
    this.setState({
      subreddit: {
        value: selectedValue,
        validationStatus: 'success'
      }
    })
  }

  handleFileRemove() {
    this.setState({
      file: {
        value: null,
        validationStatus: ''
      }
    })
  }

  handleFileUpload = ({ file, onSuccess, onError }) => {
    const sizeInMb = file.size / 1024 / 1024

    // Simulating Ant-Design action responses with setTimeout.
    if (sizeInMb > 10) {
      setTimeout(() => {
        onError(FILE_TOO_BIG);
      }, 0);
      this.setState({
        file: {
          value: null,
          validationStatus: 'error'
        }
      })
      return;
    }

    this.setState({
      file: {
        value: file,
        validationStatus: 'success'
      }
    })
    setTimeout(() => {
      onSuccess("ok");
    }, 0);
  };

  isFormInvalid() {
    return !(this.state.title.validateStatus === 'success' &&
      this.state.url.validateStatus !== 'error' &&
      this.state.file.validationStatus !== 'error' &&
      this.state.subreddit.validationStatus === 'success'
    );
  }

  render() {
    const { title, url, file, loading, subreddit, options, content, imageUrl } = this.state;

    return (
      <div className="post-container">
        <h1 className="page-title">Edit Post</h1>
        <div className="post-content">
          <Form onSubmit={this.handleSubmit} className="post-form">
            <FormItem label="Title"
              hasFeedback
              required
              validateStatus={title.validateStatus}
              help={title.errorMsg}>
              <Input
                size="large"
                name="title"
                autoComplete="off"
                placeholder="Post Title"
                value={title.value}
                onChange={(event) => this.handleInputChange(event, this.validateTitle)} />
            </FormItem>
            <FormItem label="Content">
              <TextArea
                name="content"
                autosize={{ minRows: 6 }}
                placeholder="Content"
                value={content.value}
                onChange={(event) => this.handleInputChange(event, () => void 0)} />
            </FormItem>
            <FormItem
              label="Subreddit"
              required>
              <Select
                showSearch
                placeholder="Select a subreddit"
                value={subreddit.value}
                optionFilterProp="children"
                onChange={this.handleSelectionChange}
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }>
                {options}
              </Select>
            </FormItem>
            <FormItem label="url"
              hasFeedback
              validateStatus={url.validateStatus}
              help={url.errorMsg}>
              <Input
                size="large"
                name="url"
                autoComplete="off"
                placeholder="URL"
                disabled={file.value !== null}
                value={url.value}
                onChange={(event) => this.handleInputChange(event, this.validateUrl)} />
            </FormItem>
            <div className="file-content" style={{ display: imageUrl ? 'block' : 'none' }}>
              <iframe src={imageUrl}
                title="unique?"
                allowFullScreen="yes"
                scrolling="no"
                frameBorder="0"
              />
            </div>
            <FormItem label="File/Image">
              <Dragger
                name="file"
                disabled={url.value.length > 0}
                onRemove={this.handleFileRemove}
                customRequest={this.handleFileUpload}>
                <p className="ant-upload-drag-icon">
                  <Icon type="inbox" />
                </p>
                <p className="ant-upload-text">Click or drag file to this area to upload</p>
                <p className="ant-upload-text">10MB size limit</p>
              </Dragger>
            </FormItem>
            <FormItem>
              <Button type="primary"
                htmlType="submit"
                size="large"
                loading={loading}
                className="post-form-button"
                disabled={this.isFormInvalid()}>Edit</Button>
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
  };

  validateUrl = (url) => {
    const URL_REGEX = RegExp(/(http|https):\/\/.+/gm);

    if (url.length === 0) {
      return {
        validateStatus: '',
        errorMsg: null
      }
    }

    if (!URL_REGEX.test(url)) {
      return {
        validateStatus: 'error',
        errorMsg: INVALID_URL
      }
    }

    return {
      validateStatus: 'success',
      errorMsg: null
    }
  };
}

export default EditPost;