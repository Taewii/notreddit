import React, { Component } from 'react';

import PostList from '../post/PostList';

class SubredditPosts extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.dataLoadingFunction = this.props.dataLoadingFunction;
    this.subreddit = this.props.match.params.subreddit;
  }

  render() {
    return (
      <div>
        <PostList
          isAuthenticated={this.isAuthenticated}
          dataLoadingFunction={this.dataLoadingFunction}
          username={this.subreddit}
          {...this.props}
        />
      </div>
    )
  }
}

export default SubredditPosts;