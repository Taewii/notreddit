import React, { Component } from 'react';

import CommentList from '../comment/CommentList';
import ProfileDetailsMenu from './ProfileDetailsMenu';

class UserComments extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.username = this.props.match.params.username;
    this.currentUser = this.props.currentUser;
    this.currentUserUsername = '';

    if (this.currentUser !== null) {
      this.currentUserUsername = this.currentUser.username
    }
  }

  render() {
    return (
      <div>
        <ProfileDetailsMenu username={this.username} currentUser={this.currentUserUsername} />
        <CommentList
          isAuthenticated={this.isAuthenticated}
          username={this.username}
          {...this.props}
        />
      </div>
    )
  }
}

export default UserComments;