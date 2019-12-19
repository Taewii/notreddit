import React, { Component } from 'react';
import './AllUsers.css';

import {
  getAllUsersWithRoles,
  handleRoleChange,
  getCurrentUser,
  getAllRoles,
  deleteUser
} from '../../services/userService';
import { successNotification, errorNotification } from '../../util/notifications';

import { List, Select, Skeleton, Popconfirm, Button } from 'antd';

class AllUsers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      initLoading: true,
      loading: false,
      data: [],
      roles: [],
      currentUser: {}
    };

    this.handleChange = this.handleChange.bind(this);
    this.getUserRoleOptions = this.getUserRoleOptions.bind(this);
  }

  componentDidMount() {
    Promise.all([getAllUsersWithRoles(), getCurrentUser(), getAllRoles()])
      .then(res => {
        this.setState({
          initLoading: false,
          data: res[0].users,
          currentUser: res[1],
          roles: res[2].roles
        });
      }).catch(error => errorNotification(error));
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

    this.state.roles.forEach(role => {
      const shouldBeDisabled = role === currentRole || role === 'ROOT';
      optionList.push(<Select.Option key={role} disabled={shouldBeDisabled}>{role}</Select.Option>)
    });

    return (
      <Select
        defaultValue={currentRole}
        disabled={
          user.id === this.state.currentUser.id
          || user.roles[0] === 'ROOT'
          || (!this.state.currentUser.roles.includes('ROOT') && user.roles[0] === 'ADMIN')
        }
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
    const { initLoading, data, currentUser } = this.state;

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
            {currentUser.roles.includes('ROOT')
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