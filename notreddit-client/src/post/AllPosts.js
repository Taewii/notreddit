import React, { Component } from 'react';
import './AllPosts.css';

import { List, Icon, notification, Tooltip } from 'antd';
import { allPosts } from '../services/postService';
import { timeSince } from '../util/APIUtils';

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
    this.state = {
      initLoading: true,
      loading: false,
      data: []
    };
  }

  componentDidMount() {
    this._isMounted = true;

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

  upvote = () => {
    // TODO
  };

  downvote = () => {
    // TODO
  };

  render() {
    const { data, action } = this.state;

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
            key={post.id}
            actions={[
              <span key="comment-basic-upvote">
                <Tooltip title="Upvote">
                  <Icon
                    type="like"
                    theme={action === 'upvoted' ? 'filled' : 'outlined'}
                    onClick={this.upvote}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme={action === 'downvoted' ? 'filled' : 'outlined'}
                    onClick={this.downvote}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.downvotes}</span>
              </span>,
              <span key="comments">
                <IconText type="message" text="2" />
              </span>,
            ]}
          >
            <List.Item.Meta
              avatar={
                <a href={'/post/' + post.id}>
                  <img src={post.fileThumbnailUrl === null ? 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpuQW-Yaooyex01Istft3iPtUz5kSjb4UdtMrxjKp0b-JEWIMl' : post.fileThumbnailUrl} width="128px" alt="thumbnail" />
                </a>
              }
              title={<a href={'/post/' + post.id}>{post.title}</a>}
              description={
                <span>
                  submitted {timeSince(post.createdAt)} ago by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
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