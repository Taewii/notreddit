import React, { Component } from 'react';
import './App.css';
import {
  Route,
  withRouter,
  Switch
} from 'react-router-dom';

import { getCurrentUser } from '../services/userService';
import { ACCESS_TOKEN } from '../util/constants';
import PrivateRoute from '../common/PrivateRoute';

import Login from '../user/Login'
import Signup from '../user/Signup'
import AllUsers from '../user/AllUsers';
import AppHeader from '../common/AppHeader';
import NotFound from '../common/NotFound';
import LoadingIndicator from '../common/LoadingIndicator';
import SubredditCreate from '../subreddit/SubredditCreate';
import CreatePost from '../post/CreatePost';
import AllPosts from '../post/AllPosts';
import PostDetails from '../post/PostDetails';

import { Layout, notification } from 'antd';
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

  handleLogout(redirectTo = "/", notificationType = "success", description = "You're successfully logged out.") {
    localStorage.removeItem(ACCESS_TOKEN);

    this.setState({
      currentUser: null,
      isAuthenticated: false
    });

    this.props.history.push(redirectTo);

    notification[notificationType]({
      message: 'notreddit',
      description: description,
    });
  }

  handleLogin() {
    notification.success({
      message: 'notreddit',
      description: "You're successfully logged in.",
    });
    this.loadCurrentUser();
    this.props.history.push("/");
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
              <PrivateRoute
                path="/login"
                component={(props) => <Login onLogin={this.handleLogin} {...props} />}
                authenticated={!this.state.isAuthenticated}
                redirectPath="/"
              />
              <PrivateRoute
                path="/signup"
                component={Signup}
                authenticated={!this.state.isAuthenticated}
                redirectPath="/"
              />
              <PrivateRoute
                path="/user/all"
                component={AllUsers}
                authenticated={this.hasRole('ADMIN')}
                redirectPath="/"
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
              <Route path="/post/:id" component={PostDetails} />
              <Route path="/" component={AllPosts} />
              <Route component={NotFound} />
            </Switch>
          </div>
        </Content>
      </Layout>
    );
  }
}

export default withRouter(App);
