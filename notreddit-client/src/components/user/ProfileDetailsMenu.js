import React, { Component } from 'react';
import './ProfileDetailsMenu.css';

import { Link, withRouter } from 'react-router-dom';
import { Menu, Icon } from 'antd';

class ProfileDetailsMenu extends Component {
  constructor(props) {
    super(props);
    this.username = this.props.username;
    this.currentUser = this.props.currentUser;
  }

  render() {
    let menuItems = [
      <Menu.Item key={`/user/${this.username}/posts`}>
        <Link to={`/user/${this.username}/posts`}>
          <Icon type="snippets" /> Posts
      </Link>
      </Menu.Item>,
      <Menu.Item key={`/user/${this.username}/comments`}>
        <Link to={`/user/${this.username}/comments`}>
          <Icon type="message" /> Comments
      </Link>
      </Menu.Item>
    ];


    if (this.username === this.currentUser) {
      const upvotesDownvotesMenu = [
        <Menu.Item key={`/user/${this.username}/upvoted`}>
          <Link to={`/user/${this.username}/upvoted`}>
            <Icon type="arrow-up" /> Upvoted
          </Link>
        </Menu.Item>,
        <Menu.Item key={`/user/${this.username}/downvoted`}>
          <Link to={`/user/${this.username}/downvoted`}>
            <Icon type="arrow-down" /> Downvoted
          </Link>
        </Menu.Item>
      ];

      menuItems = menuItems.concat(upvotesDownvotesMenu);
    }

    return (
      <>
        <h1 className="user-profile-info-title">{this.username}'s profile</h1>
        <Menu
          style={{ marginBottom: '20px' }}
          onClick={this.handleClick}
          selectedKeys={[this.props.location.pathname, this.props.location.pathname + '/posts']}
          mode="horizontal">
          {menuItems}
        </Menu>
      </>
    );
  }
}

export default withRouter(ProfileDetailsMenu);