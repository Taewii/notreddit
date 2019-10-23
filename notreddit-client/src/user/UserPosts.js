import React, { Component } from 'react';

import PostList from '../post/PostList';
import ProfileDetailsMenu from './ProfileDetailsMenu';

class UserPosts extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.dataLoadingFunction = this.props.dataLoadingFunction;
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
        <PostList
          isAuthenticated={this.isAuthenticated}
          dataLoadingFunction={this.dataLoadingFunction}
          username={this.username}
          {...this.props}
        />
      </div>
    )
  }
}

export default UserPosts;