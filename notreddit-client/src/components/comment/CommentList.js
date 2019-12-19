import React, { Component } from 'react';
import './CommentList.css';

import { Link } from 'react-router-dom';
import { List, Icon, Tooltip, Avatar, Popconfirm, Modal, Form, Input, Select } from 'antd';

import { IconText } from '../../util/IconText';
import { errorNotification, successNotification } from '../../util/notifications';
import { timeSince } from '../../util/util';
import { voteForComment } from '../../services/voteService';
import { getUserVotesForComments } from '../../services/voteService';
import { commentsByUsername, deleteCommentById, editComment } from '../../services/commentService';

const { Option } = Select;

class CommentList extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.username = this.props.username;
    this.currentUser = this.props.currentUser;
    this.userIsModerator = false;
    this.votes = {};
    this.state = {
      initLoading: true,
      loading: false,
      totalComments: 0,
      comments: [],
      page: 0,
      pageSize: 10,
      sort: '',
      modalIsVisible: false,
      editCommentContent: '',
      editCommentId: null
    };

    if (this.currentUser !== null) {
      this.userIsModerator = this.currentUser.roles.includes('MODERATOR');
    }

    this.deleteComment = this.deleteComment.bind(this);
    this.colorVote = this.colorVote.bind(this);
    this.hideModal = this.hideModal.bind(this);
    this.showModal = this.showModal.bind(this);
    this.handleCommentEdit = this.handleCommentEdit.bind(this);
    this.handleInputChange = this.handleInputChange.bind(this);
  }

  componentDidMount() {
    this._isMounted = true;
    const isAuthenticated = this.props.isAuthenticated;

    const searchParams = new URLSearchParams(this.props.location.search);
    const page = searchParams.get('page') - 1 || 0;
    const pageSize = searchParams.get('pageSize') || 10;
    const sort = searchParams.get('sort') || '';

    this.loadComments(page, pageSize, sort);

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

  loadComments(page, pageSize, sort) {
    commentsByUsername(this.username, page, pageSize, sort)
      .then(res => {
        if (this._isMounted) {
          this.setState({
            comments: res.comments,
            totalComments: res.total,
            page: page + 1,
            pageSize: +pageSize,
            sort
          });
        }
      }).catch(error => errorNotification(error));
  }

  deleteComment(commentId) {
    deleteCommentById(commentId)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount();
      }).catch(error => errorNotification(error));
  }

  handleCommentEdit(event) {
    event.preventDefault();
    const commentData = {
      content: this.state.editCommentContent,
      commentId: this.state.editCommentId
    };

    editComment(commentData)
      .then(res => {
        successNotification(res.message);
        this.hideModal();
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  showModal(commentId, commentContent) {
    this.setState({
      editCommentContent: commentContent,
      editCommentId: commentId,
      modalIsVisible: true
    });
  }

  hideModal() {
    this.setState({
      editCommentId: null,
      editCommentContent: '',
      modalIsVisible: false
    })
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

  handleInputChange(event) {
    const target = event.target;
    const inputName = target.name;
    const inputValue = target.value;

    this.setState({
      [inputName]: inputValue
    });
  }

  render() {
    const { comments, totalComments, page, pageSize, sort } = this.state;

    return (
      <>
        <Select
          showSearch
          style={{ width: 200 }}
          className="sorting-options"
          placeholder="Order by"
          value={sort || 'Order by'}
          onChange={(value) => {
            this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}&sort=${value}`);
          }}
          filterOption={(input, option) =>
            option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
          }
        >
          <Option value="createdOn,desc">Newest</Option>
          <Option value="createdOn">Oldest</Option>
          <Option value="upvotes">Upvotes (Low &#8594; High)</Option>
          <Option value="upvotes,desc">Upvotes (High &#8594; Low)</Option>
          <Option value="downvotes">Downvotes (Low &#8594; High)</Option>
          <Option value="downvotes,desc">Downvotes (High &#8594; Low)</Option>
        </Select>
        <br />
        <List
          bordered
          itemLayout="vertical"
          size="small"
          pagination={{
            showSizeChanger: true,
            total: totalComments,
            hideOnSinglePage: true,
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
              actions={actions(comment, this.currentUser, this.deleteComment, this.showModal, this.userIsModerator)}
            >
              <List.Item.Meta
                avatar={
                  <Link to={'/post/' + comment.postId}>
                    <Avatar src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPkZuKBUk2AttQ5hVKzFSDUJe0sfgS66rRnrUNrJlX4X1ugtD91Q" />
                  </Link>
                }
                title={<a href={'/post/' + comment.postId}>{comment.postTitle}</a>}
                description={
                  comment.creatorEnabled ?
                    <span>
                      submitted {timeSince(comment.createdOn)} by <a href={'/user/' + comment.creatorUsername}>{comment.creatorUsername}</a>
                    </span>
                    :
                    <span>
                      submitted {timeSince(comment.createdOn)} by <span className="deleted-creator">[deleted]</span>
                    </span>
                }
              />
              <span className="content">{comment.content}</span>
            </List.Item>
          )}
        />
        <Modal
          title="Edit comment"
          okText="Edit"
          cancelText="Discard"
          visible={this.state.modalIsVisible}
          onOk={this.handleCommentEdit}
          onCancel={this.hideModal}
          okButtonProps={{ disabled: this.state.editCommentContent.trim().length === 0 }}
        >
          <Form>
            <Form.Item hasFeedback onSubmit={this.handleCommentEdit}>
              <Input.TextArea
                className="comment-edit-textarea"
                value={this.state.editCommentContent}
                size="large"
                name="editCommentContent"
                onChange={this.handleInputChange}
              />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

const actions = (comment, currentUser, deleteComment, showModal, userIsModerator) => {
  let actions = [
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
      <Link to={'/post/' + comment.postId} style={{ color: 'gray' }}>
        <IconText type="message" text={comment.replies} />
      </Link>
    </span>,
  ];

  if (currentUser.username === comment.creatorUsername) {
    const editAndDelete = [
      <span key="edit-comment" onClick={showModal.bind(this, comment.id, comment.content)}>
        <IconText type="edit" text="Edit" />
      </span>,
      <Popconfirm
        title="Are you sure you want to delete this comment?"
        onConfirm={deleteComment.bind(this, comment.id)}
      >
        <span key="remove-comment">
          <IconText type="delete" text="Delete" />
        </span>
      </Popconfirm>
    ];

    actions = actions.concat(editAndDelete);
  } else if (userIsModerator) {
    actions.push(
      <Popconfirm
        title="Are you sure you want to delete this comment?"
        onConfirm={deleteComment.bind(this, comment.id)}
      >
        <span key="remove-comment">
          <IconText type="delete" text="Delete" />
        </span>
      </Popconfirm>
    );
  }

  return actions;
};

export default CommentList;