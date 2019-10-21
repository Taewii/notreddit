import React, { Component } from 'react';
import './App.css';

import { Route, withRouter, Redirect, Switch } from 'react-router-dom';
import { Layout, notification } from 'antd';

import { ACCESS_TOKEN } from '../util/constants';
import { successNotification } from '../util/notifications';
import { getCurrentUser } from '../services/userService';
import { allPosts, postsByUsername } from '../services/postService';

import Login from '../user/Login';
import Signup from '../user/Signup';
import AllUsers from '../user/AllUsers';
import UserDetails from '../user/UserDetails';
import AppHeader from '../common/AppHeader';
import NotFound from '../common/NotFound';
import PrivateRoute from '../common/PrivateRoute';
import LoadingIndicator from '../common/LoadingIndicator';
import SubredditCreate from '../subreddit/SubredditCreate';
import CreatePost from '../post/CreatePost';
import PostList from '../post/PostList';
import PostDetails from '../post/PostDetails';
import CommentDetails from '../comment/CommentDetails';

const { Content } = Layout;

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentUser: null,
      isAuthenticated: false,
      isLoading: false,
      roles: [],
    }
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
    getCurrentUser()
      .then(response => {
        this.setState({
          currentUser: response,
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
    successNotification('You\'re successfully logged out.')
  }

  handleLogin() {
    successNotification('You\'re successfully logged in.')
    this.loadCurrentUser();
    this.props.history.push('/home');
  }

  hasRole(role) {
    return this.state.isAuthenticated && this.state.currentUser.roles.includes(role);
  }

  render() {
    if (this.state.isLoading) {
      return <LoadingIndicator />
    }

    return (
      <Layout className="app-container">
        <AppHeader isAuthenticated={this.state.isAuthenticated}
          currentUser={this.state.currentUser}
          onLogout={this.handleLogout} />

        <Content className="app-content" style={{ textAlign: "center" }}>
          <div className="container">
            <Switch>
              <Redirect exact from="/" to="/home" />
              <PrivateRoute
                path="/login"
                component={(props) => <Login onLogin={this.handleLogin} {...props} />}
                authenticated={!this.state.isAuthenticated}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/signup"
                component={Signup}
                authenticated={!this.state.isAuthenticated}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/user/all"
                component={AllUsers}
                authenticated={this.hasRole('ADMIN')}
                redirectPath="/home"
              />
              <PrivateRoute
                path="/subreddit/create"
                component={SubredditCreate}
                authenticated={this.state.isAuthenticated}
              />
              <PrivateRoute
                path="/post/create"
                component={CreatePost}
                authenticated={this.state.isAuthenticated}
              />
              <Route path="/post/:id" component={(props) =>
                <PostDetails isAuthenticated={this.state.isAuthenticated} {...props} />}
              />
              <Route exact path="/home" component={(props) =>
                <PostList
                  isAuthenticated={this.state.isAuthenticated}
                  dataLoadingFunction={allPosts}
                  username={null}
                  {...props}
                />}
              />
              <Route exact path={['/user/:username', '/user/:username/posts']} component={(props) =>
                <UserDetails
                  isAuthenticated={this.state.isAuthenticated}
                  dataLoadingFunction={postsByUsername}
                  {...props}
                />}
              />
              <Route path="/user/:username/comments" component={(props) =>
                <CommentDetails
                  isAuthenticated={this.state.isAuthenticated}
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
