import React, { Component } from 'react';
import './PostDetails.css';

import { Link } from 'react-router-dom';
import { List, Icon, notification, Tooltip } from 'antd';
import { findById } from '../services/postService';
import { timeSince } from '../util/APIUtils';
import { getVoteForPost } from '../services/voteService';

const Content = ({ fileUrl }) => {
  return (
    <div className="file-content">
      <iframe src={fileUrl}
        title="unique?"
        allowFullScreen="yes"
        scrolling="no"
        frameBorder="0"
      />
    </div>
  )
}

class PostDetails extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.state = {
      post: {}
    }
  }

  componentDidMount() {
    this._isMounted = true;
    const { id } = this.props.match.params;

    findById(id)
      .then(res => {
        if (this._isMounted) {
          this.setState({ post: res })
        }
      })
      .catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      })

    getVoteForPost(id)
      .then(res => {
        if (res.hasVoted) {
          // TODO
        }
      })
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    const { post } = this.state;

    return (
      <div className="container">
        <List
          itemLayout="vertical"
          size="large">
          <List.Item
            // onLoad={(event) => this.colorVote(event, post.id)}
            key={post.id}
            actions={[
              <span key="comment-basic-upvote">
                <Tooltip title="Upvote">
                  <Icon
                    type="like"
                    theme="outlined"
                  // onClick={(event) => this.vote(event, 1, post.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme="outlined"
                  // onClick={(event) => this.vote(event, -1, post.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.downvotes}</span>
              </span>
            ]}
          >
            <List.Item.Meta
              avatar={
                <Link to={'/post/' + post.id}>
                  <img src={post.fileThumbnailUrl === null ? 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpuQW-Yaooyex01Istft3iPtUz5kSjb4UdtMrxjKp0b-JEWIMl' : post.fileThumbnailUrl} width="128px" alt="thumbnail" />
                </Link>
              }
              title={<a href={'/post/' + post.id}>{post.title}</a>}
              description={
                <span>
                  submitted {timeSince(post.createdAt)} ago by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                </span>
              }
            />
          </List.Item>
        </List>
        <Content fileUrl={post.fileUrl} />
      </div >
    )
  }
}

export default PostDetails;