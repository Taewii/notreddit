import React, { Component } from 'react';
import './CommentList.css';

import { Link } from 'react-router-dom';
import { List, Icon, Tooltip } from 'antd';

import { errorNotification } from '../util/notifications';
import { voteForComment } from '../services/voteService';
import { timeSince } from '../util/APIUtils';
import { getUserVotesForComments } from '../services/userService';
import { commentsByUsername } from '../services/commentService';

const IconText = ({ type, text }) => (
  <span>
    <Icon type={type} style={{ marginRight: 8 }} />
    {text}
  </span>
);

class CommentList extends Component {
  constructor(props) {
    super(props)
    this._isMounted = false;
    this.username = this.props.username;
    this.votes = {};
    this.state = {
      initLoading: true,
      loading: false,
      totalComments: 0,
      comments: [],
      page: 0,
      pageSize: 10
    };

    this.colorVote = this.colorVote.bind(this);
  }

  loadComments(page, pageSize) {
    commentsByUsername(this.username, page, pageSize)
      .then(res => {
        if (this._isMounted) {
          this.setState({
            comments: res.comments,
            totalComments: res.total,
            page: page + 1,
            pageSize: +pageSize
          });
        }
      }).catch(error => errorNotification(error))
  }

  componentDidMount() {
    this._isMounted = true;
    const isAuthenticated = this.props.isAuthenticated;

    const searchParams = new URLSearchParams(this.props.location.search);
    const page = searchParams.get('page') - 1 || 0;
    const pageSize = searchParams.get('pageSize') || 10;

    this.loadComments(page, pageSize);

    if (isAuthenticated) {
      getUserVotesForComments()
        .then(res => {
          this.votes = res || {}
        }).catch(error => errorNotification(error));
    }

    this.setState({
      initLoading: false,
    });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  colorVote(event, commentId) {
    const icons = event.currentTarget.querySelectorAll('svg');
    const vote = this.votes[commentId];

    // clear all the colors first and the color them correctly
    icons[0].setAttribute('color', '');
    icons[1].setAttribute('color', '');

    if (vote === 1) {
      icons[0].setAttribute('color', 'green');
    } else if (vote === -1) {
      icons[1].setAttribute('color', 'red');
    }
  }

  render() {
    const { comments, totalComments, page, pageSize } = this.state;

    return (
      <List
        bordered
        itemLayout="vertical"
        size="small"
        pagination={{
          showSizeChanger: true,
          total: totalComments,
          defaultCurrent: 1,
          current: page,
          pageSize: pageSize,
          onChange: (page, pageSize) => {
            this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
          },
          onShowSizeChange: (page, pageSize) => {
            this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
          }
        }}
        dataSource={comments}
        renderItem={comment => (
          <List.Item
            className="comment-item"
            onLoad={(event) => this.colorVote(event, comment.id)}
            key={comment.id}
            actions={[
              <span key="comment-basic-upvote">
                <Tooltip title="Upvote">
                  <Icon
                    type="like"
                    theme="outlined"
                    onClick={(event) => voteForComment(event, 1, comment.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{comment.upvotes}</span>
              </span>,
              <span key="comment-basic-downvote">
                <Tooltip title="Downvote">
                  <Icon
                    type="dislike"
                    theme="outlined"
                    onClick={(event) => voteForComment(event, -1, comment.id)}
                  />
                </Tooltip>
                <span style={{ paddingLeft: 8, cursor: 'auto' }}>{comment.downvotes}</span>
              </span>,
              <span key="comments">
                <Link to={'/post/' + comment.id} style={{ color: 'gray' }}>
                  <IconText type="message" text={comment.replies} />
                </Link>
              </span>,
            ]}
          >
            <List.Item.Meta
              title={<a href={'/post/' + comment.postId}>{comment.postTitle}</a>}
              description={
                <span>
                  submitted {timeSince(comment.createdOn)} by <a href={'/user/' + comment.creatorUsername}>{comment.creatorUsername}</a>
                </span>
              }
            />
            <span className="content">{comment.content}</span>
          </List.Item>
        )}
      />
    );
  }
}

export default CommentList;