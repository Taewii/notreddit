import React, { Component } from 'react';

import CommentList from './CommentList';
import ProfileDetailsMenu from '../user/ProfileDetailsMenu';

class CommentDetails extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.username = this.props.match.params.username;
  }

  render() {
    return (
      <div>
        <ProfileDetailsMenu username={this.username} />
        <CommentList
          isAuthenticated={this.isAuthenticated}
          username={this.username}
          {...this.props}
        />
      </div>
    )
  }
}

export default CommentDetails;