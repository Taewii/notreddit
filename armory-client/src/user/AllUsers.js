import React, { Component } from 'react';
import './AllUsers.css';

import { getAllUsersWithRoles } from '../util/APIUtils'

import { List, Icon, Menu, Button, Dropdown, Skeleton, message } from 'antd';

class AllUsers extends Component {
  _isMounted = false;

  state = {
    initLoading: true,
    loading: false,
    data: [],
  };

  componentDidMount() {
    this._isMounted = true;

    getAllUsersWithRoles()
      .then(res => {
        if (this._isMounted) {
          this.setState({
            initLoading: false,
            data: res.users,
          });
        }
      });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  handleMenuClick(e) {
    message.info('Click on menu item.');
    console.log('click', e);
  }

  render() {
    const { initLoading, data } = this.state;

    const menu = (
      <Menu onClick={this.handleMenuClick}>
        <Menu.Item key="1">
          <Icon type="user" />
          ROOT
        </Menu.Item>
        <Menu.Item key="2">
          <Icon type="user" />
          ADMIN
        </Menu.Item>
        <Menu.Item key="3">
          <Icon type="user" />
          MODERATOR
        </Menu.Item>
        <Menu.Item key="3">
          <Icon type="user" />
          USER
        </Menu.Item>
      </Menu>
    );

    return (
      <List
        className="user-list"
        loading={initLoading}
        itemLayout="horizontal"
        bordered={true}
        dataSource={data}
        renderItem={user => (
          <List.Item
            actions={[<button key="user-edit">edit</button>, <button key="user-delete">delete</button>]}
          >
            <Skeleton avatar title={false} loading={user.loading} active>
              <List.Item.Meta
                title={user.username}
                description={user.id}
              />
              <Dropdown overlay={menu} className="users-dropdown">
                <Button>
                  Moderator <Icon type="down" />
                </Button>
              </Dropdown>
            </Skeleton>
          </List.Item>
        )}
      />
    );
  }
}

export default AllUsers;