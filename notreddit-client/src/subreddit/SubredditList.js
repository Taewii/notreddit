import React, { Component } from 'react';
import './SubredditList.css';

import { List, Button } from 'antd';

import { successNotification, errorNotification } from '../util/notifications';
import {
  getAllSubredditsWithPostsCount,
  getUserSubscriptions,
  subscribe,
  unsubscribe
} from '../services/subredditService';

class SubredditList extends Component {
  constructor(props) {
    super(props)
    this._isMounted = false;
    this.state = {
      initLoading: true,
      loading: false,
      data: [],
      subscriptions: {}
    };

    this.subscribe = this.subscribe.bind(this);
    this.unsubscribe = this.unsubscribe.bind(this);
  }

  componentDidMount() {
    this._isMounted = true;

    getUserSubscriptions()
      .then(res => {
        this.setState({
          subscriptions: res.reduce((current, item) => {
            current[item] = true;
            return current;
          }, {})
        });
      })
      .catch(error => errorNotification(error));

    getAllSubredditsWithPostsCount()
      .then(res => {
        if (this._isMounted) {
          this.setState({
            initLoading: false,
            data: res,
          });
        }
      }).catch(error => errorNotification(error));
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
    const { initLoading, data } = this.state;

    return (
      <List
        className="user-list"
        loading={initLoading}
        itemLayout="horizontal"
        bordered={true}
        dataSource={data}
        renderItem={subreddit => (
          <List.Item>
            <List.Item.Meta
              title={<a style={{ color: "#1890ff" }} href={`/subreddit/${subreddit.title}`}>{subreddit.title}</a>}
              description={`${subreddit.title} currently has ${subreddit.postCount} ${subreddit.postCount === 1 ? 'post' : 'posts'}.`} />
            {
              this.state.subscriptions[subreddit.title]
                ? <Button className="subreddit-button" type="danger"
                  onClick={(e) => this.unsubscribe(subreddit.title)}>Unsubscribe</Button>
                : <Button className="subreddit-button" type="primary"
                  onClick={(e) => this.subscribe(subreddit.title)}>Subscribe</Button>
            }
          </List.Item >
        )
        }
      />
    );
  }
}

export default SubredditList;