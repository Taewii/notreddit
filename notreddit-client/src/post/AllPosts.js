import React, { Component } from 'react';
import './AllPosts.css';

import { List, Icon, notification, Tooltip } from 'antd';
import { allPosts, vote } from '../services/postService';
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

    this.vote = this.vote.bind(this);
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

  handleVoteChange(event, choice) {
    const targetUl = event.currentTarget.parentElement.parentElement.parentElement;
    const spans = targetUl.querySelectorAll('span');
    const upvoteLi = spans[0];
    const downvoteLi = spans[2];

    const upvoteSvg = upvoteLi.querySelector('svg');
    const upvoteSpan = upvoteLi.querySelector('span');

    const downvoteSvg = downvoteLi.querySelector('svg');
    const downvoteSpan = downvoteLi.querySelector('span');

    const isUpvoted = !!upvoteSvg.getAttribute('color');
    const isDownvoted = !!downvoteSvg.getAttribute('color');

    if (isUpvoted) {
      upvoteSpan.textContent = +upvoteSpan.textContent - 1;
    } else if (isDownvoted) {
      downvoteSpan.textContent = +downvoteSpan.textContent - 1;
    }

    if (isUpvoted && choice === 1) {
      upvoteSvg.setAttribute('color', '');
      return;
    } else if (isDownvoted && choice === -1) {
      downvoteSvg.setAttribute('color', '');
      return;
    }

    if (choice === 1) {
      upvoteSpan.textContent = +upvoteSpan.textContent + 1;
      upvoteSvg.setAttribute('color', 'green');
      downvoteSvg.setAttribute('color', '');
    } else {
      upvoteSvg.setAttribute('color', '');
      downvoteSvg.setAttribute('color', 'red');
      downvoteSpan.textContent = +downvoteSpan.textContent + 1;
    }
  }

  vote(event, choice, postId) {
    this.handleVoteChange(event, choice)
    vote(choice, postId)
      .catch(error => {
        notification.error({
          message: 'notreddit',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      });
  };

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
                    onClick={(event) => this.vote(event, 1, post.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{post.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme="outlined"
                    onClick={(event) => this.vote(event, -1, post.id)}
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