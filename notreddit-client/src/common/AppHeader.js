import React, { Component } from 'react';
import {
  Link,
  withRouter
} from 'react-router-dom';
import './AppHeader.css';
import { Layout, Menu, Dropdown, Icon } from 'antd';
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
        <Menu.Item key="/">
          <Link to="/">
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
            <Link to="/">notreddit</Link>
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
      <Menu.Item key="all-users" className="dropdown-item">
        <Link to="/subreddit/create">Create</Link>
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
        <Link to={`/users/${props.currentUser.username}`}>Profile</Link>
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
        <Icon type="user" className="nav-icon" style={{ marginRight: 0 }} /> <Icon type="down" />
      </button>
    </Dropdown>
  );
}


export default withRouter(AppHeader);