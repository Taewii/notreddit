import React, { Component } from 'react';
import './AllUsers.css';

import {
  getAllUsersWithRoles,
  handleRoleChange,
  getCurrentUser,
  getAllRoles,
  deleteUser
} from '../services/userService';
import { successNotification, errorNotification } from '../util/notifications';

import { List, Select, Skeleton, Popconfirm, Button } from 'antd';

class AllUsers extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.roles = [];
    this.currentUser = {};
    this.state = {
      initLoading: true,
      loading: false,
      data: [],
    };

    getCurrentUser()
      .then(res => this.currentUser = res);

    getAllRoles()
      .then(res => this.roles = res.roles);

    this.handleChange = this.handleChange.bind(this);
    this.getUserRoleOptions = this.getUserRoleOptions.bind(this);
  }

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
      }).catch(error => errorNotification(error));
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  handleChange(userId, currentRole, newRole) {
    handleRoleChange({ userId, currentRole, newRole })
      .then(res => {
        successNotification(res.message);
        this.componentDidMount(); // reload component
      }).catch(error => errorNotification(error));
  }

  getUserRoleOptions(user) {
    const optionList = [];
    const currentRole = user.roles[0];

    this.roles.forEach(role => {
      const shouldBeDisabled = role === currentRole || role === 'ROOT';
      optionList.push(<Select.Option key={role} disabled={shouldBeDisabled}>{role}</Select.Option>)
    });

    return (
      <Select
        defaultValue={currentRole}
        disabled={user.id === this.currentUser.id || user.roles[0] === 'ROOT'}
        className="users-dropdown"
        onChange={e => this.handleChange(user.id, currentRole, e)}>
        {optionList}
      </Select>
    )
  }

  handleDeleteConfirm(userId) {
    deleteUser(userId)
      .then(res => {
        successNotification(res.message);
        this.componentDidMount(); // reload component
      }).catch(error => errorNotification(error))
  }

  render() {
    const { initLoading, data } = this.state;

    return (
      <List
        className="user-list"
        loading={initLoading}
        itemLayout="horizontal"
        bordered={true}
        dataSource={data}
        renderItem={user => (
          <List.Item className="user-list-item">
            <Skeleton avatar title={false} loading={user.loading} active>
              <List.Item.Meta
                title={user.username}
                description={user.id} />
              {this.getUserRoleOptions(user)}
            </Skeleton>
            {this.currentUser.roles.includes('ROOT')
              ? <Popconfirm
                title="Are you sure you want to delete this user?"
                placement="topRight"
                onConfirm={e => this.handleDeleteConfirm(user.id)}
                okText="Yes"
                cancelText="No"
                disabled={user.roles.includes('ROOT') ? true : false}
              >
                <Button
                  className="list-button"
                  disabled={user.roles.includes('ROOT') ? true : false}>delete</Button>
              </Popconfirm>
              : null}
          </List.Item>
        )}
      />
    );
  }
}

export default AllUsers;