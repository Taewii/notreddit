import React, { Component } from 'react';

import { Link, withRouter } from 'react-router-dom';
import { Menu, Icon } from 'antd';

class ProfileDetailsMenu extends Component {
  constructor(props) {
    super(props);
    this.username = this.props.username;
  }

  render() {
    return (
      <Menu
        style={{ marginBottom: '20px' }}
        onClick={this.handleClick}
        selectedKeys={[this.props.location.pathname, this.props.location.pathname + '/posts']}
        mode="horizontal">
        <Menu.Item key={`/user/${this.username}/posts`}>
          <Link to={`/user/${this.username}/posts`}>
            <Icon type="snippets" /> Posts
          </Link>
        </Menu.Item>
        <Menu.Item key={`/user/${this.username}/comments`}>
          <Link to={`/user/${this.username}/comments`}>
            <Icon type="message" /> Comments
          </Link>
        </Menu.Item>
      </Menu>
    );
  }
}

export default withRouter(ProfileDetailsMenu);