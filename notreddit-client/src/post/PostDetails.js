import React, { Component } from 'react';
import './PostDetails.css';

import { Link } from 'react-router-dom';
import { List, Icon, Tooltip, Button, Form, Input, Comment, Avatar, Modal, Popconfirm, Select } from 'antd';

import { errorNotification, successNotification } from '../util/notifications'
import { timeSince, getAvatarColor } from '../util/util';
import { IconText } from '../util/IconText';
import { findById, deletePostById } from '../services/postService';
import { getVoteForPost, voteForPost, voteForComment } from '../services/voteService';
import { comment, findCommentsForPost, deleteCommentById, editComment } from '../services/commentService';
import { getUserVotesForComments } from '../services/voteService';

const { Option } = Select;

class PostDetails extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.commentVotes = {};
    this.userIsModerator = false;
    this.state = {
      post: {},
      comments: [],
      postId: '',
      replyCommentId: null,
      commentContent: '',
      replyCommentContent: '',
      editCommentContent: '',
      replyModalIsVisible: false,
      editModalIsVisible: false,
      sort: ''
    };

    this.currentUser = this.props.currentUser;
    this.currentUserUsername = '';

    if (this.currentUser !== null) {
      this.currentUserUsername = this.currentUser.username
      this.userIsModerator = this.currentUser.roles.includes('MODERATOR');
    }

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleCommentReply = this.handleCommentReply.bind(this);
    this.handleCommentEdit = this.handleCommentEdit.bind(this);
    this.showReplyModalAndSaveCommentId = this.showReplyModalAndSaveCommentId.bind(this);
    this.showEditModalAndSaveCommentIdAndContent = this.showEditModalAndSaveCommentIdAndContent.bind(this);
    this.hideReplyModal = this.hideReplyModal.bind(this);
    this.hideEditModal = this.hideEditModal.bind(this);
    this.deletePost = this.deletePost.bind(this);
    this.deleteComment = this.deleteComment.bind(this);
  }

  componentDidMount() {
    this._isMounted = true;
    const { id } = this.props.match.params;
    const isAuthenticated = this.props.isAuthenticated;

    const searchParams = new URLSearchParams(this.props.location.search);
    const sort = searchParams.get('sort') || '';

    const promises = [findById(id), findCommentsForPost(id, sort)];

    // if user is logged in get user related properties
    if (isAuthenticated) {
      promises.push(getVoteForPost(id))
      promises.push(getUserVotesForComments())
    }

    Promise.all(promises)
      .then(res => {
        const post = res[0];
        const comments = res[1];
        const vote = res[2];
        this.commentVotes = res[3];

        if (isAuthenticated && vote.hasVoted) {
          this.colorVote(vote.choice)
        }

        if (this._isMounted) {
          this.setState({
            post,
            sort,
            comments,
            postId: id
          })
        }
      })
      .catch(error => errorNotification(error))
  }

  colorVote(choice) {
    const icons = document.querySelectorAll('.post svg');

    if (icons.length >= 2) { // sometimes it the async function loads before the icons are rendered
      if (choice === 1) {
        icons[0].setAttribute('color', 'green');
      } else if (choice === -1) {
        icons[1].setAttribute('color', 'red');
      }
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

  handleCommentEdit(event) {
    event.preventDefault();
    const commentData = {
      content: this.state.editCommentContent,
      commentId: this.state.replyCommentId
    };


    editComment(commentData)
      .then(res => {
        successNotification(res.message);
        this.hideEditModal();
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  handleCommentReply(event) {
    event.preventDefault();
    const commentData = {
      postId: this.state.postId,
      content: this.state.commentContent ? this.state.commentContent : this.state.replyCommentContent,
      parentId: this.state.replyCommentId
    };

    if (!this.state.replyCommentId) {
      event.target.reset();
    }

    comment(commentData)
      .then(res => {
        successNotification(res.message);
        this.setState({ commentContent: '' })
        this.hideReplyModal();
        this.componentDidMount(); // not sure how to force update otherwise..
      })
      .catch(error => errorNotification(error));
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  showReplyModalAndSaveCommentId(commentId) {
    this.setState({
      replyCommentId: commentId,
      replyModalIsVisible: true
    });
  }

  showEditModalAndSaveCommentIdAndContent(commentId, commentContent) {
    this.setState({
      editCommentContent: commentContent,
      replyCommentId: commentId,
      editModalIsVisible: true
    });
  }

  hideEditModal() {
    const modalTextArea = document.querySelector('.comment-edit-textarea');
    if (modalTextArea) {
      modalTextArea.value = '';
    }

    this.setState({
      replyCommentId: null,
      editModalIsVisible: false,
      editCommentContent: ''
    })
  }

  hideReplyModal() {
    const modalTextArea = document.querySelector('.comment-reply-textarea');
    if (modalTextArea) {
      modalTextArea.value = '';
    }

    this.setState({
      replyCommentId: null,
      replyModalIsVisible: false,
      replyCommentContent: ''
    })
  }

  deletePost(postId) {
    deletePostById(postId)
      .then(res => {
        successNotification(res.message)
        this.props.history.push('/home');
      }).catch(error => errorNotification(error));
  }

  deleteComment(commentId) {
    deleteCommentById(commentId)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount();
      }).catch(error => errorNotification(error));
  }

  render() {
    const {
      post, postId, sort, commentContent, replyModalIsVisible, comments,
      replyCommentContent, editCommentContent, editModalIsVisible
    } = this.state;

    let actions = [
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
    ];

    if (this.currentUserUsername === post.creatorUsername) {
      const editAndDelete = [
        <span key="edit-comment">
          <Link style={{ color: "inherit" }} to={`/post/edit/${postId}`}>
            <IconText type="edit" text="Edit" />
          </Link>
        </span>,
        <Popconfirm
          title="Are you sure you want to delete this post?"
          onConfirm={this.deletePost.bind(this, postId)}
        >
          <span key="remove-comment">
            <IconText type="delete" text="Delete" />
          </span>
        </Popconfirm>
      ];

      actions = actions.concat(editAndDelete);
    } else if (this.userIsModerator) {
      actions.push(
        <Popconfirm
          title="Are you sure you want to delete this post?"
          onConfirm={this.deletePost.bind(this, postId)}
        >
          <span key="remove-comment">
            <IconText type="delete" text="Delete" />
          </span>
        </Popconfirm>
      )
    }

    return (
      <div className="post">
        <List key="asd"
          bordered
          itemLayout="vertical"
          size="large">
          <List.Item
            key={post.id}
            actions={actions}
          >
            <List.Item.Meta
              title={post.title}
              avatar={
                <a href={post.fileUrl}>
                  <img src={post.fileThumbnailUrl === null ? 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpuQW-Yaooyex01Istft3iPtUz5kSjb4UdtMrxjKp0b-JEWIMl' : post.fileThumbnailUrl} width="64px" alt="thumbnail" />
                </a>
              }
              description={
                post.creatorEnabled ?
                  <span>
                    submitted {timeSince(post.createdOn)} by <a href={'/user/' + post.creatorUsername}>{post.creatorUsername}</a> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                  </span>
                  :
                  <span>
                    submitted {timeSince(post.createdOn)} by <span className="deleted-creator">[deleted]</span> to <a href={'/subreddit/' + post.subredditTitle}>{'r/' + post.subredditTitle}</a>
                  </span>
              }
            />
          </List.Item>
          {post.content ? <Content content={post.content} /> : null}
          <List.Item>
            <Form onSubmit={this.handleCommentReply}>
              <Form.Item hasFeedback>
                <Input.TextArea
                  disabled={!this.props.isAuthenticated}
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
                  disabled={commentContent.trim().length === 0}
                >Submit comment</Button>
              </Form.Item>
            </Form>
          </List.Item>
        </List>
        {post.fileUrl ? <FileContent fileUrl={post.fileUrl} /> : null}
        <Select
          showSearch
          style={{ width: 200 }}
          className={comments.length > 0 ? 'comment-sorting-options' : 'hidden'}
          size="small"
          placeholder="Order by"
          value={sort || 'Order by'}
          onChange={(value) => {
            this.props.history.push(`${window.location.pathname}?&sort=${value}`);
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
        {comments.map(comment =>
          <CommentComponent
            key={comment.id}
            comment={comment}
            votes={this.commentVotes || {}}
            showReplyModal={this.showReplyModalAndSaveCommentId}
            showEditModal={this.showEditModalAndSaveCommentIdAndContent}
            currentUser={this.currentUserUsername}
            deleteComment={this.deleteComment}
            userIsModerator={this.userIsModerator}
          />)}
        <Modal
          title="Reply to comment"
          okText="Reply"
          cancelText="Discard"
          visible={replyModalIsVisible}
          onOk={this.handleCommentReply}
          onCancel={this.hideReplyModal}
          okButtonProps={{ disabled: replyCommentContent.trim().length === 0 }}
        >
          <Form>
            <Form.Item hasFeedback onSubmit={this.handleCommentReply}>
              <Input.TextArea
                disabled={!this.props.isAuthenticated}
                className="comment-reply-textarea"
                size="large"
                name="replyCommentContent"
                placeholder="Write your reply here."
                onChange={this.handleInputChange}
              />
            </Form.Item>
          </Form>
        </Modal>
        <Modal
          title="Edit comment"
          okText="Edit"
          cancelText="Discard"
          visible={editModalIsVisible}
          onOk={this.handleCommentEdit}
          onCancel={this.hideEditModal}
          okButtonProps={{ disabled: editCommentContent.trim().length === 0 }}
        >
          <Form>
            <Form.Item hasFeedback onSubmit={this.handleCommentEdit}>
              <Input.TextArea
                disabled={!this.props.isAuthenticated}
                className="comment-edit-textarea"
                value={editCommentContent}
                size="large"
                name="editCommentContent"
                onChange={this.handleInputChange}
              />
            </Form.Item>
          </Form>
        </Modal>
      </div >
    )
  }
}

const CommentComponent = ({ comment, votes, showReplyModal, showEditModal, currentUser, deleteComment, userIsModerator }) => {
  let upvoteColor = '';
  let downvoteColor = '';
  const vote = votes[comment.id];

  if (vote) {
    if (vote === 1) {
      upvoteColor = 'green';
    } else if (vote === -1) {
      downvoteColor = 'red';
    }
  }

  let actions = [
    <span key="comment-basic-upvote">
      <Tooltip title="Upvote">
        <Icon
          style={{ color: upvoteColor }}
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
          style={{ color: downvoteColor }}
          type="dislike"
          theme="outlined"
          onClick={(event) => voteForComment(event, -1, comment.id)}
        />
      </Tooltip>
      <span style={{ paddingLeft: 8, cursor: 'auto' }}>{comment.downvotes}</span>
    </span>,
    <span onClick={showReplyModal.bind(this, comment.id)} key="comment-basic-reply-to">Reply to</span>,
  ];

  if (currentUser === comment.creatorUsername) {
    const editAndDelete = [
      <span key="edit-comment" onClick={showEditModal.bind(this, comment.id, comment.content)}>
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
    )
  }

  return (
    <Comment
      key={comment.id}
      author={
        comment.creatorEnabled
          ? <a href={`/user/${comment.creatorUsername}`}>{comment.creatorUsername}</a>
          : <span className="deleted-creator">[deleted]</span>
      }
      content={comment.content}
      datetime={timeSince(comment.createdOn).replace('ago', '')}
      actions={actions}
      avatar={
        <Avatar style={{ backgroundColor: getAvatarColor(comment.creatorUsername), verticalAlign: 'middle' }}>
          {comment.creatorUsername[0].toUpperCase()}
        </Avatar>
      }
    >
      {comment.children.length > 0 && comment.children.map(child => {
        return <CommentComponent
          showReplyModal={showReplyModal}
          showEditModal={showEditModal}
          votes={votes}
          key={child.id}
          comment={child}
          currentUser={currentUser}
          deleteComment={deleteComment}
          userIsModerator={userIsModerator}
        />
      })}
    </Comment>
  )
};

const Content = ({ content }) => (
  <List.Item>
    {content}
  </List.Item>
);

const FileContent = ({ fileUrl }) => (
  <div className="file-content">
    <iframe src={fileUrl}
      title="unique?"
      allowFullScreen="yes"
      scrolling="no"
      frameBorder="0"
    />
  </div>
);

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
};

export default PostDetails;

