import React, { Component } from 'react';

import PostList from '../post/PostList';
import ProfileDetailsMenu from './ProfileDetailsMenu';

class UserDetails extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.dataLoadingFunction = this.props.dataLoadingFunction;
    this.username = this.props.match.params.username;
  }

  render() {
    return (
      <div>
        <ProfileDetailsMenu username={this.username} />
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

export default UserDetails;