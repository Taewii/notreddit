import React, { Component } from 'react';
import './AllPosts.css';

import { Link } from 'react-router-dom';
import { List, Icon, notification, Tooltip } from 'antd';
import { allPosts } from '../services/postService';
import { voteForPost } from '../services/voteService';
import { timeSince } from '../util/APIUtils';
import { getUserVotes } from '../services/userService';

const IconText = ({ type, text }) => (
  <span>
    <Icon type={type} style={{ marginRight: 8 }} />
    {text}
  </span>
);

class AllPosts extends Component {
  constructor(props) {
    super(props)
    this._isMounted = false;
    this.votes = {};
    this.state = {
      initLoading: true,
      loading: false,
      data: []
    };

    this.colorVote = this.colorVote.bind(this);
  }

  componentDidMount() {
    this._isMounted = true;

    getUserVotes()
      .then(res => this.votes = res);

    allPosts()
      .then(res => {
        if (this._isMounted) {
          this.setState({
            initLoading: false,
            data: res
          });
        }
      }).catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  colorVote(event, postId) {
    const icons = event.currentTarget.querySelectorAll('svg');
    const vote = this.votes[postId];

    if (vote === 1) {
      icons[0].setAttribute('color', 'green');
    } else if (vote === -1) {
      icons[1].setAttribute('color', 'red');
    }
  }

  render() {
    const { data } = this.state;
    return (
      <List
        bordered
        itemLayout="vertical"
        size="large"
        pagination={{
          onChange: page => {
            console.log(page);
          },
          pageSize: 5,
        }}
        dataSource={data}
        renderItem={post => (
          <List.Item
            onLoad={(event) => this.colorVote(event, post.id)}
            key={post.id}
            actions={[
              <span key="comment-basic-upvote">
                <Tooltip title="Upvote">
                  <Icon
                    type="like"
                    theme="outlined"
                    onClick={(event) => voteForPost(event, 1, post.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme="outlined"
                    onClick={(event) => voteForPost(event, -1, post.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.downvotes}</span>
              </span>,
              <span key="comments">
                <Link to={'/post/' + post.id} style={{ color: 'gray' }}>
                  <IconText type="message" text={post.commentCount} />
                </Link>
              </span>,
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
                  submitted {timeSince(post.createdOn)} by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                </span>
              }
            />
          </List.Item>
        )}
      />
    );
  }
}

export default AllPosts;