import React, { Component } from 'react';
import './PostDetails.css';

import { List, Icon, notification, Tooltip, Button, Form, Input } from 'antd';

import { findById } from '../services/postService';
import { timeSince } from '../util/APIUtils';
import { getVoteForPost, voteForPost } from '../services/voteService';
import { comment, findCommentsForPost } from '../services/commentService';

class PostDetails extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.state = {
      post: {},
      comments: [],
      postId: '',
      commentContent: ''
    }

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidMount() {
    this._isMounted = true;
    const { id } = this.props.match.params;

    Promise.all([findById(id), findCommentsForPost(id), getVoteForPost(id)])
      .then(res => {
        const post = res[0];
        const comments = res[1];
        const vote = res[2];

        if (vote.hasVoted) {
          this.colorVote(vote.choice)
        }

        if (this._isMounted) {
          this.setState({
            post,
            comments,
            postId: id
          })
        }
      })
      .catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      })
  }

  colorVote(choice) {
    const icons = document.querySelectorAll('.post svg');

    if (icons.length === 2) { // sometimes it the async function loads before the icons are rendered
      if (choice === 1) {
        icons[0].setAttribute('color', 'green');
      } else if (choice === -1) {
        icons[1].setAttribute('color', 'red');
      }
    }
  }

  handleSubmit(event) {
    event.preventDefault();
    const commentData = {
      postId: this.state.postId,
      content: this.state.commentContent,
      parentId: null, // TODO
    };
    event.target.reset();

    comment(commentData)
      .then(res => {
        if (res.success) {
          notification.success({
            message: 'notreddit',
            description: res.message
          })
        }
      })
      .catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      })
  }

  handleInputChange(event) {
    const target = event.target;
    const inputName = target.name;
    const inputValue = target.value;

    this.setState({
      [inputName]: inputValue
    });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    const { post, postId } = this.state;

    return (
      <div className="post">
        <List key="asd"
          bordered
          itemLayout="vertical"
          size="large">
          <List.Item
            key={post.id}
            actions={[
              <span key="comment-basic-upvote">
                <Tooltip title="Upvote">
                  <Icon
                    type="like"
                    theme="outlined"
                    onClick={(event) => voteForPost(event, 1, postId)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme="outlined"
                    onClick={(event) => voteForPost(event, -1, postId)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.downvotes}</span>
              </span>,
              <span>
                {
                  post.fileUrl
                    ? <Button className="show-btn" onClick={changeFileContentDisplay}>Show File</Button>
                    : null
                }
              </span>,
            ]}
          >
            <List.Item.Meta
              avatar={
                <a href={post.fileUrl}>
                  <img src={post.fileThumbnailUrl === null ? 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpuQW-Yaooyex01Istft3iPtUz5kSjb4UdtMrxjKp0b-JEWIMl' : post.fileThumbnailUrl} width="64px" alt="thumbnail" />
                </a>
              }
              title={post.title}
              description={
                <span>
                  submitted {timeSince(post.createdOn)} by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                </span>
              }
            />
          </List.Item>
          {post.content ? <Content content={post.content} /> : null}
          <List.Item>
            <Form onSubmit={this.handleSubmit}>
              <Form.Item hasFeedback>
                <Input.TextArea
                  size="large"
                  name="commentContent"
                  placeholder="Write your comment here."
                  onChange={this.handleInputChange}
                />
              </Form.Item>
              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  disabled={this.state.commentContent.trim().length === 0}
                >Submit comment</Button>
              </Form.Item>
            </Form>
          </List.Item>
        </List>
        {post.fileUrl ? <FileContent fileUrl={post.fileUrl} /> : null}
      </div >
    )
  }
}

const changeFileContentDisplay = () => {
  const div = document.querySelector('.file-content');
  const btn = document.querySelector('.show-btn');

  if (!div.style.display || div.style.display === 'none') {
    div.style.display = 'block';
    btn.textContent = 'Hide File';
  } else {
    div.style.display = 'none';
    btn.textContent = 'Show File';
  }
}

const FileContent = ({ fileUrl }) => {
  return (
    <div className="file-content">
      <iframe src={fileUrl}
        title="unique?"
        allowFullScreen="yes"
        scrolling="no"
        frameBorder="0"
      />
    </div>
  );
}

const Content = ({ content }) => {
  return (
    <List.Item>
      {content}
    </List.Item>
  );
}

export default PostDetails;

