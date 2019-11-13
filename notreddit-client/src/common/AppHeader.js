import React, { Component } from 'react';
import {
  Link,
  withRouter
} from 'react-router-dom';
import './AppHeader.css';
import { Layout, Menu, Dropdown, Icon, Badge, Divider } from 'antd';
const Header = Layout.Header;

class AppHeader extends Component {
  constructor(props) {
    super(props);
    this.handleMenuClick = this.handleMenuClick.bind(this);
  }

  handleMenuClick({ key }) {
    if (key === "logout") {
      this.props.onLogout();
    }
  }

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
        <Menu.Item key="subreddit">
          <SubredditDropdownMenu />
        </Menu.Item>,
        <Menu.Item key="/profile" className="profile-menu">
          <ProfileDropdownMenu
            mentionCount={this.props.mentionCount}
            currentUser={this.props.currentUser}
            handleMenuClick={this.handleMenuClick} />
        </Menu.Item>
      ];

      if (currentUser.roles.includes('ADMIN')) {
        menuItems.splice(menuItems.length - 1, 0,
          <Menu.Item key="/admin" className="profile-menu">
            <AdminDropdownMenu />
          </Menu.Item>
        );
      }
    } else {
      menuItems = [
        <Menu.Item key="/subreddit/all">
          <Link to="/subreddit/all">Subreddits</Link>
        </Menu.Item>,
        <Divider type="vertical" style={{ backgroundColor: "#1890ff" }} />,
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
        <div className="container">
          <div className="app-title" >
            <Link to="/home">notreddit</Link>
          </div>
          <Menu
            className="app-menu"
            mode="horizontal"
            selectedKeys={[this.props.location.pathname]}
            style={{ lineHeight: '64px' }} >
            {menuItems}
          </Menu>
        </div>
      </Header>
    );
  }
}

const AdminDropdownMenu = (props) => {
  const dropdownMenu = (
    <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
      <Menu.Item key="all-users" className="dropdown-item">
        <Link to="/user/all">All Users</Link>
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown
      overlay={dropdownMenu}
      getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
      <button className="ant-dropdown-link">
        <Icon type="security-scan" className="nav-icon" style={{ marginRight: 0 }} /> <Icon type="down" />
      </button>
    </Dropdown>
  );
}

const SubredditDropdownMenu = (props) => {
  const dropdownMenu = (
    <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
      <Menu.Item key="subreddit-create" className="dropdown-item">
        <Link to="/subreddit/create">Create</Link>
      </Menu.Item>
      <Menu.Item key="subreddit-all" className="dropdown-item">
        <Link to="/subreddit/all">All Subreddits</Link>
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown
      overlay={dropdownMenu}
      getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
      <button className="ant-dropdown-link">
        Subreddit <Icon type="down" />
      </button>
    </Dropdown>
  );
}

const ProfileDropdownMenu = (props) => {
  const dropdownMenu = (
    <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
      <Menu.Item key="user-info" className="dropdown-item" disabled>
        <div className="user-full-name-info">
          {props.currentUser.name}
        </div>
        <div className="username-info">
          @{props.currentUser.username}
        </div>
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="profile" className="dropdown-item">
        <Link to={`/user/${props.currentUser.username}`}>Profile</Link>
      </Menu.Item>
      <Menu.Item key="mentions" className="dropdown-item">
        <Badge count={props.mentionCount}>
          <Link
            style={{ color: "inherit" }}
            to={`/user/mentions`}
          >
            Mentions
          </Link>
        </Badge>
      </Menu.Item>
      <Menu.Item key="logout" className="dropdown-item">
        Logout
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown
      overlay={dropdownMenu}
      getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
      <button className="ant-dropdown-link">
        <Badge count={props.mentionCount} dot>
          <Icon type="user" className="nav-icon" style={{ marginRight: 0 }} />
          <Icon type="down" />
        </Badge>
      </button>
    </Dropdown>
  );
}

export default withRouter(AppHeader);