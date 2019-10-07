import React, { Component } from 'react';
import './PostDetails.css';

import { List, Icon, notification, Tooltip, Button, Form, Input } from 'antd';

import { findById } from '../services/postService';
import { timeSince } from '../util/APIUtils';
import { getVoteForPost, voteForPost } from '../services/voteService';

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

const CommentForm = (props) => {
  return (
    <List.Item>
      <Form onSubmit={handleSubmit}>
        <Form.Item hasFeedback>
          <Input.TextArea
            size="large"
            name="comment"
            placeholder="Write your comment here." />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
          >Submit comment</Button>
        </Form.Item>
      </Form>
    </List.Item>
  );
}

const handleSubmit = (event) => {
  // TDDO:
}

const Content = ({ content }) => {
  return (
    <List.Item>
      {content}
    </List.Item>
  );
}

class PostDetails extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.state = {
      post: {},
      postId: ''
    }
  }

  componentDidMount() {
    this._isMounted = true;
    const { id } = this.props.match.params;

    findById(id)
      .then(res => {
        if (this._isMounted) {
          this.setState({ post: res, postId: id })

          getVoteForPost(id)
            .then(res => {
              if (res.hasVoted) {
                this.colorVote(res.choice)
              }
            })
            .catch(error => {
              notification.error({
                message: 'notreddit',
                description: error.message || 'Sorry! Something went wrong. Please try again!'
              });
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

    if (choice === 1) {
      icons[0].setAttribute('color', 'green');
    } else if (choice === -1) {
      icons[1].setAttribute('color', 'red');
    }
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
                  submitted {timeSince(post.createdAt)} ago by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                </span>
              }
            />
          </List.Item>
          {post.content ? <Content content={post.content} /> : null}
          <CommentForm />
        </List>
        {post.fileUrl ? <FileContent fileUrl={post.fileUrl} /> : null}

      </div >
    )
  }
}

export default PostDetails;