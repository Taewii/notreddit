import React, { Component } from 'react';
import './App.css';

import { Route, withRouter, Redirect, Switch } from 'react-router-dom';
import { Layout, notification } from 'antd';

import {
  SUCCESSFUL_LOGOUT_MESSAGE,
  SUCCESSFUL_LOGIN_MESSAGE
} from '../util/messageConstants';
import { ACCESS_TOKEN } from '../util/constants';
import { successNotification } from '../util/notifications';
import { getCurrentUser } from '../services/userService';
import {
  postsByUsername,
  postsBySubreddit,
  allSubscribedPosts,
  allPostsFromTheDefaultSubreddits,
  getUpvotedPosts,
  getDownvotedPosts,
  allPosts
} from '../services/postService';
import { getUnreadMentionsCount } from '../services/mentionService';

import Login from '../user/Login';
import Signup from '../user/Signup';
import AllUsers from '../user/AllUsers';
import UserComments from '../user/UserComments';
import UserPosts from '../user/UserPosts';
import AppHeader from '../common/AppHeader';
import NotFound from '../common/NotFound';
import PrivateRoute from '../common/PrivateRoute';
import LoadingIndicator from '../common/LoadingIndicator';
import SubredditCreate from '../subreddit/SubredditCreate';
import SubredditPosts from '../subreddit/SubredditPosts';
import SubredditList from '../subreddit/SubredditList';
import CreatePost from '../post/CreatePost';
import EditPost from '../post/EditPost';
import PostList from '../post/PostList';
import PostDetails from '../post/PostDetails';
import MentionList from '../mention/MentionList';
import Chat from '../chat/Chat';

const { Content } = Layout;

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentUser: null,
      isAuthenticated: false,
      isLoading: true,
      roles: [],
      mentionCount: 0
    };
    this.handleLogout = this.handleLogout.bind(this);
    this.handleLogin = this.handleLogin.bind(this);

    notification.config({
      placement: 'topLeft',
      top: 70,
      duration: 2,
    });
  }

  componentDidMount() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.setState({
      isLoading: true
    });

    Promise.all([getCurrentUser(), getUnreadMentionsCount()])
      .then(res => {
        if (!res[0].enabled) {
          this.handleLogout();
          return; //TODO: do I need to return?
        }

        this.setState({
          currentUser: res[0],
          mentionCount: res[1],
          isAuthenticated: true,
          isLoading: false
        });
      }).catch(error => {
        this.setState({
          isLoading: false
        });
      });
  }

  handleLogout() {
    localStorage.removeItem(ACCESS_TOKEN);

    this.setState({
      currentUser: null,
      isAuthenticated: false
    });

    this.props.history.push('/home');
    successNotification(SUCCESSFUL_LOGOUT_MESSAGE)
  }

  handleLogin() {
    successNotification(SUCCESSFUL_LOGIN_MESSAGE);
    this.loadCurrentUser();
    this.props.history.push('/home');
  }

  hasRole(role) {
    return this.state.isAuthenticated && this.state.currentUser.roles.includes(role);
  }

  render() {
    const { isLoading, isAuthenticated, currentUser, mentionCount } = this.state;

    if (isLoading) {
      return <LoadingIndicator />
    }

    return (
      <Layout className="app-container">
        <AppHeader
          mentionCount={mentionCount}
          isAuthenticated={isAuthenticated}
          currentUser={currentUser}
          onLogout={this.handleLogout} />

        <Content className="app-content" style={{ textAlign: "center" }}>
          <div className="container">
            <Switch>
              <Redirect exact from="/" to="/home" />
              <PrivateRoute
                path="/login"
                component={(props) => <Login onLogin={this.handleLogin} {...props} />}
                authenticated={!isAuthenticated}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/signup"
                component={Signup}
                authenticated={!isAuthenticated}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/user/all"
                component={AllUsers}
                authenticated={this.hasRole('ADMIN')}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/post/all"
                authenticated={this.hasRole('ADMIN')}
                redirectPath="/home"
                isAuthenticated={isAuthenticated}
                dataLoadingFunction={allPosts}
                currentUser={currentUser}
                username={null}
                component={PostList}
              />
              <PrivateRoute
                path="/subreddit/create"
                component={SubredditCreate}
                authenticated={isAuthenticated}
              />
              <PrivateRoute
                path="/post/create"
                component={CreatePost}
                authenticated={isAuthenticated}
              />
              <PrivateRoute
                path="/user/mentions"
                component={MentionList}
                authenticated={isAuthenticated}
              />
              <PrivateRoute
                path="/chat"
                currentUser={currentUser}
                authenticated={isAuthenticated}
                component={Chat}
              />
              <Route exact path="/post/:id" component={(props) =>
                <PostDetails
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                  {...props} />}
              />
              <PrivateRoute
                path="/post/edit/:id"
                authenticated={isAuthenticated}
                component={EditPost}
              />
              <Route exact path="/home" component={(props) =>
                <PostList
                  isAuthenticated={isAuthenticated}
                  dataLoadingFunction={isAuthenticated ? allSubscribedPosts : allPostsFromTheDefaultSubreddits}
                  currentUser={currentUser}
                  username={null}
                  {...props}
                />}
              />
              <Route exact path={['/user/:username', '/user/:username/posts']} component={(props) =>
                <UserPosts
                  isAuthenticated={isAuthenticated}
                  dataLoadingFunction={postsByUsername}
                  currentUser={currentUser}
                  {...props}
                />}
              />
              <Route path="/user/:username/comments" component={(props) =>
                <UserComments
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                  {...props}
                />}
              />
              <Route path="/user/:username/upvoted" component={(props) =>
                <UserPosts
                  isAuthenticated={isAuthenticated}
                  dataLoadingFunction={getUpvotedPosts}
                  currentUser={currentUser}
                  {...props}
                />}
              />
              <Route path="/user/:username/downvoted" component={(props) =>
                <UserPosts
                  isAuthenticated={isAuthenticated}
                  dataLoadingFunction={getDownvotedPosts}
                  currentUser={currentUser}
                  {...props}
                />}
              />
              <Route path="/subreddit/all" component={(props) =>
                <SubredditList
                  isAuthenticated={isAuthenticated}
                  {...props}
                />}
              />
              <Route path="/subreddit/:subreddit" component={(props) =>
                <SubredditPosts
                  isAuthenticated={isAuthenticated}
                  dataLoadingFunction={postsBySubreddit}
                  currentUser={currentUser}
                  {...props}
                />}
              />
              <Route component={NotFound} />
            </Switch>
          </div>
        </Content>
      </Layout>
    );
  }
}

export default withRouter(App);
