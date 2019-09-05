import React, { Component } from 'react';
import './AllUsers.css';

import { getAllUsersWithRoles, handleRoleChange, getCurrentUser } from '../util/APIUtils'

import { List, Select, Skeleton, Button, notification } from 'antd';

class AllUsers extends Component {
  constructor(props) {
    super(props)
    this._isMounted = false;
    this.roles = [];
    this.currentUserId = '';
    this.state = {
      initLoading: true,
      loading: false,
      data: [],
    };

    getCurrentUser()
      .then(res => this.currentUserId = res.id);

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
      }).catch(error => {
        this.props.history.push('/');
        notification.error({
          message: 'WoW Armory',
          description: error.message || 'Sorry! Something went wrong. Please try again!'
        });
      });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  handleChange(userId, currentRole, newRole) {
    handleRoleChange({ userId, currentRole, newRole })
      .then(res => {
        if (res.success) {
          notification.success({
            message: 'WoW Armory',
            description: res.message
          });
          this.props.history.push("/user/all"); // TODO: figure out how to reload the component
        } else {
          notification.error({
            message: 'WoW Armory',
            description: res.message
          });
        }
      });
  }

  getUserRoleOptions(user) {
    if (!this.roles.length) {
      this.roles = user.roles;
    }

    const optionList = [];
    const currentRole = user.roles[0];

    this.roles.forEach(role => {
      const shouldBeDisabled = role === currentRole || role === 'ROOT';
      optionList.push(<Select.Option key={role} disabled={shouldBeDisabled}>{role}</Select.Option>)
    })

    return (
      <Select
        defaultValue={currentRole}
        disabled={user.id === this.currentUserId}
        className="users-dropdown"
        onChange={e => this.handleChange(user.id, currentRole, e)}>
        {optionList}
      </Select>
    )
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
          <List.Item>
            <Skeleton avatar title={false} loading={user.loading} active>
              <List.Item.Meta
                title={user.username}
                description={user.id} />
              {this.getUserRoleOptions(user)}
            </Skeleton>
            <Button className="list-button">delete</Button>
          </List.Item>
        )}
      />
    );
  }
}

export default AllUsers;