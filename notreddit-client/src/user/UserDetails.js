import React, { Component } from 'react';

import { Tabs } from 'antd';

import PostList from '../post/PostList';

const { TabPane } = Tabs;

class UserDetails extends Component {
  constructor(props) {
    super(props);
    this.isAuthenticated = this.props.isAuthenticated;
    this.dataLoadingFunction = this.props.dataLoadingFunction;
    this.username = this.props.match.params.username;
  }

  callback(key) {
    console.log(key);
  }

  render() {
    return (
      <Tabs defaultActiveKey="posts" onChange={this.callback}>
        <TabPane tab="Posts" key="1">
          <PostList
            isAuthenticated={this.isAuthenticated}
            dataLoadingFunction={this.dataLoadingFunction}
            username={this.username}
            {...this.props}
          />
        </TabPane>
        <TabPane tab="Comments" key="comments">
          Content of Tab Pane 2
        </TabPane>
      </Tabs>
    );
  }
}

export default UserDetails;