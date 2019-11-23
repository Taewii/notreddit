import React, { Component } from 'react';
import {
  Link,
  withRouter
} from 'react-router-dom';
import './AppHeader.css';

import { Layout, Menu, Icon, Badge, Divider, Drawer, Button } from 'antd';

const Header = Layout.Header;
const SubMenu = Menu.SubMenu;

class AppHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false
    };

    this.showDrawer = this.showDrawer.bind(this);
    this.onClose = this.onClose.bind(this);
  }

  showDrawer() {
    this.setState({
      visible: true,
    });
  };

  onClose() {
    this.setState({
      visible: false,
    });
  };

  render() {
    let menuItems;
    const currentUser = this.props.currentUser;
    if (currentUser) {
      menuItems = [
        <Menu.Item key="/home">
          <Link to="/home">
            <Icon type="home" className="nav-icon" />
          </Link>
        </Menu.Item>,
        <Menu.Item key="/post/create">
          <Link to="/post/create">Create Post</Link>
        </Menu.Item>,
        <SubMenu key="subreddit-submenu" title={
          <span>
            <span>Subreddit</span>
            <Icon style={{ marginLeft: "5px", marginRight: "0" }} className="down-arrow" type="down" />
          </span>
        }>
          <Menu.Item key="subreddit-create" className="dropdown-item">
            <Link to="/subreddit/create">Create</Link>
          </Menu.Item>
          <Menu.Item key="subreddit-all" className="dropdown-item">
            <Link to="/subreddit/all">All Subreddits</Link>
          </Menu.Item>
        </SubMenu>,
        <SubMenu key="profile-submenu" title={
          <span>
            <Badge count={this.props.mentionCount} dot>
              <Icon type="user" className="nav-icon" style={{ marginRight: 0 }} />
              <Icon type="down" className="down-arrow" />
            </Badge>
          </span>
        }>
          <Menu.Item key="user-info" className="dropdown-item" disabled>
            <div className="user-full-name-info">
              {this.props.currentUser.name}
            </div>
            <div className="username-info">
              @{this.props.currentUser.username}
            </div>
          </Menu.Item>
          <Menu.Item key="profile" className="dropdown-item">
            <Link to={`/user/${this.props.currentUser.username}`}>Profile</Link>
          </Menu.Item>
          <Menu.Item key="mentions" className="dropdown-item">
            <Badge count={this.props.mentionCount}>
              <Link
                style={{ color: "inherit" }}
                to={`/user/mentions`}
              >
                Mentions
          </Link>
            </Badge>
          </Menu.Item>
          <Menu.Item key="logout" className="dropdown-item" onClick={this.props.onLogout}>
            Logout
          </Menu.Item>
        </SubMenu>
      ];

      if (currentUser.roles.includes('ADMIN')) {
        menuItems.splice(menuItems.length - 1, 0,
          <SubMenu key="admin-submenu" title={
            <span>
              <Icon type="safety-certificate" className="nav-icon" style={{ marginRight: 0 }} />
              <Icon type="down" className="down-arrow" />
            </span>
          }>
            <Menu.Item key="all-users" className="dropdown-item">
              <Link to="/user/all">All Users</Link>
            </Menu.Item>
            <Menu.Item key="all-posts" className="dropdown-item">
              <Link to="/post/all">All Posts</Link>
            </Menu.Item>
          </SubMenu>
        );
      }
    } else {
      menuItems = [
        <Menu.Item key="/subreddit/all">
          <Link to="/subreddit/all">Subreddits</Link>
        </Menu.Item>,
        <Menu.Item key="divider" className="divider" disabled={true}>
          <Divider type="vertical" style={{ backgroundColor: "#1890ff" }} />
        </Menu.Item>,
        <Menu.Item key="/login">
          <Link to="/login">Login</Link>
        </Menu.Item>,
        <Menu.Item key="/signup">
          <Link to="/signup">Signup</Link>
        </Menu.Item>
      ];
    }

    return (
      <Header className="app-header">
        <div className="container menuCon">
          <div className="app-title" >
            <Link to="/home">notreddit</Link>
          </div>
          <div className="normal-menu">
            <Menu
              className="app-menu"
              mode="horizontal"
              selectedKeys={[this.props.location.pathname]}
              style={{ lineHeight: '64px' }} >
              {menuItems}
            </Menu>
          </div>
          <Button className="barsMenu" type="primary" onClick={this.showDrawer}>
            <span className="barsBtn"></span>
          </Button>
          <Drawer
            title="Menu"
            placement="right"
            closable={false}
            onClose={this.onClose}
            visible={this.state.visible}
          >
            <Menu
              onClick={this.onClose}
              mode="inline"
              selectedKeys={[this.props.location.pathname]}
              style={{ lineHeight: '64px' }} >
              {menuItems}
            </Menu>
          </Drawer>
        </div>
      </Header >
    );
  }
}

export default withRouter(AppHeader);