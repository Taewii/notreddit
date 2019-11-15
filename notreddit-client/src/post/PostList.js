import React, { Component } from 'react';
import './PostList.css';

import { Link } from 'react-router-dom';
import { List, Icon, Tooltip, Popconfirm } from 'antd';

import { IconText } from '../util/IconText';
import { errorNotification, successNotification } from '../util/notifications';
import { voteForPost } from '../services/voteService';
import { timeSince } from '../util/APIUtils';
import { getUserVotesForPosts } from '../services/voteService';
import { deletePostById } from '../services/postService';

class PostList extends Component {
  constructor(props) {
    super(props)
    this.dataLoadingFunction = this.props.dataLoadingFunction;
    this.username = this.props.username;
    this.currentUser = this.props.currentUser;
    this.currentUserUsername = '';
    this.userIsModerator = false;
    this._isMounted = false;
    this.votes = {};
    this.state = {
      initLoading: true,
      loading: false,
      totalPosts: 0,
      posts: [],
      page: 0,
      pageSize: 10
    };

    if (this.currentUser !== null) {
      this.currentUserUsername = this.currentUser.username
      this.userIsModerator = this.currentUser.roles.includes('MODERATOR');
    }

    this.deletePost = this.deletePost.bind(this);
    this.colorVote = this.colorVote.bind(this);
  }

  loadPosts(page, pageSize) {
    this.dataLoadingFunction(page, pageSize, this.username)
      .then(res => {
        if (this._isMounted) {
          this.setState({
            posts: res.posts,
            totalPosts: res.total,
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

    this.loadPosts(page, pageSize);

    if (isAuthenticated) {
      getUserVotesForPosts()
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

  colorVote(event, postId) {
    const icons = event.currentTarget.querySelectorAll('svg');
    const vote = this.votes[postId];

    // clear all the colors first and the color them correctly
    icons[0].setAttribute('color', '');
    icons[1].setAttribute('color', '');

    if (vote === 1) {
      icons[0].setAttribute('color', 'green');
    } else if (vote === -1) {
      icons[1].setAttribute('color', 'red');
    }
  }

  deletePost(postId) {
    deletePostById(postId)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount();
      }).catch(error => errorNotification(error));
  }

  render() {
    const { posts, totalPosts, page, pageSize } = this.state;

    return (
      <List
        bordered
        itemLayout="vertical"
        size="large"
        pagination={{
          showSizeChanger: true,
          total: totalPosts,
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
        dataSource={posts}
        renderItem={post => (
          <List.Item
            onLoad={(event) => this.colorVote(event, post.id)}
            key={post.id}
            actions={actions(post, this.currentUserUsername, this.deletePost, this.userIsModerator)}
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

const actions = (post, currentUser, deletePost, userIsModerator) => {
  let actions = [
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
  ]

  if (post.creatorUsername === currentUser) {
    const editAndDelete = [
      // <span key="edit-comment">
      //   <IconText type="edit" text="Edit" />
      // </span>,
      <Popconfirm
        title="Are you sure you want to delete this post?"
        onConfirm={deletePost.bind(this, post.id)}
      >
        <span key="remove-comment">
          <IconText type="delete" text="Delete" />
        </span>
      </Popconfirm>
    ]


    actions = actions.concat(editAndDelete);
  } else if (userIsModerator) {
    actions.push(
      <Popconfirm
        title="Are you sure you want to delete this post?"
        onConfirm={deletePost.bind(this, post.id)}
      >
        <span key="remove-comment">
          <IconText type="delete" text="Delete" />
        </span>
      </Popconfirm>
    )
  }

  return actions;
}

export default PostList;