import React, { Component } from 'react';
import './SubredditPosts.css';

import { Button } from 'antd';

import PostList from '../post/PostList';

import { isUserSubscribedToSubreddit, subscribe, unsubscribe } from '../services/subredditService';
import { successNotification, errorNotification } from '../util/notifications';

class SubredditPosts extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.isAuthenticated = this.props.isAuthenticated;
    this.dataLoadingFunction = this.props.dataLoadingFunction;
    this.subreddit = this.props.match.params.subreddit;
    this.state = {
      isSubscribed: false
    }
  }

  componentDidMount() {
    this._isMounted = true;

    if (this.isAuthenticated) {
      isUserSubscribedToSubreddit(this.subreddit)
        .then(res => {
          if (this._isMounted) {
            this.setState({ isSubscribed: res.isSubscribed })
          }
        })
        .catch(error => errorNotification(error));
    }
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  subscribe(subreddit) {
    subscribe(subreddit)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  unsubscribe(subreddit) {
    unsubscribe(subreddit)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  render() {
    const ActionButtons = () => {
      if (!this.isAuthenticated) return null;

      return this.state.isSubscribed
        ? <Button className="subreddit-posts-button" type="danger"
          onClick={(e) => this.unsubscribe(this.subreddit)}>Unsubscribe</Button>
        : <Button className="subreddit-posts-button" type="primary"
          onClick={(e) => this.subscribe(this.subreddit)}>Subscribe</Button>
    };

    return (
      <>
        <div className="header-container">
          <h1 className="subreddit-posts-title">r/{this.subreddit}</h1>
          <ActionButtons />
        </div>
        <PostList
          isAuthenticated={this.isAuthenticated}
          dataLoadingFunction={this.dataLoadingFunction}
          username={this.subreddit}
          {...this.props}
        />
      </>
    )
  };
}

export default SubredditPosts;