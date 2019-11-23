import React, { Component } from 'react';
import './MentionList.css';

import { List, Button } from 'antd';

import { IconText } from '../util/IconText';
import { errorNotification, successNotification } from '../util/notifications';
import { timeSince } from '../util/APIUtils';
import { getUsersMentions, markAsRead, markAsUnread } from '../services/mentionService';

class MentionList extends Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.state = {
      initLoading: true,
      loading: false,
      mentions: [],
      page: 0,
      pageSize: 10
    };
  }

  loadMentions(page, pageSize) {
    getUsersMentions(page, pageSize)
      .then(res => {
        if (this._isMounted) {
          this.setState({
            mentions: res.mentions,
            totalMentions: res.total,
            page: page + 1,
            pageSize: +pageSize
          });
        }
      }).catch(error => errorNotification(error))
  }

  componentDidMount() {
    this._isMounted = true;

    const searchParams = new URLSearchParams(this.props.location.search);
    const page = searchParams.get('page') - 1 || 0;
    const pageSize = searchParams.get('pageSize') || 10;

    this.loadMentions(page, pageSize);

    this.setState({
      initLoading: false,
    });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  markAsRead(mentionId) {
    markAsRead(mentionId)
      .then(res => {
        if (res.success) {
          successNotification(res.message);
        } else {
          errorNotification(res.message)
        }
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  markAsUnread(mentionId) {
    markAsUnread(mentionId)
      .then(res => {
        if (res.success) {
          successNotification(res.message);
        } else {
          errorNotification(res.message)
        }
        this.componentDidMount();
      })
      .catch(error => errorNotification(error));
  }

  render() {
    const { mentions, page, pageSize, totalMentions } = this.state;

    return (
      <List
        bordered
        itemLayout="vertical"
        size="small"
        pagination={{
          showSizeChanger: true,
          total: totalMentions,
          defaultCurrent: 1,
          current: page,
          pageSize: pageSize,
          onChange: (page, pageSize) => {
            this.loadMentions(page - 1, pageSize);
            this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
          },
          onShowSizeChange: (page, pageSize) => {
            this.loadMentions(page - 1, pageSize);
            this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
          }
        }}
        dataSource={mentions}
        renderItem={mention => (
          <List.Item
            className="mention-item"
            key={mention.id}
            actions={[
              <span className="mention-read" key="mention-read">
                <Button disabled={mention.read} onClick={() => this.markAsRead(mention.id)}>
                  <IconText type="check" text="Mark as read" />
                </Button>
              </span>,
              <span key="mention-unread">
                <Button disabled={!mention.read} onClick={() => this.markAsUnread(mention.id)}>
                  <IconText type="close" text="Mark as unread" />
                </Button>
              </span>
            ]}
          >
            <List.Item.Meta
              title={
                <span>
                  <span className="mention-type">comment reply</span>
                  <a href={'/post/' + mention.commentPostId}>{mention.commentPostTitle}</a>
                  <span className={"is-read" + (!mention.read ? ' hidden' : '')}>read</span>
                </span>}
              description={
                <span>
                  from <a href={'/user/' + mention.creatorUsername}>{mention.creatorUsername}</a> sent {(timeSince(mention.createdOn)).slice(0, -4)}
                </span>
              }
            />
            <span className="content">{mention.commentContent}</span>
          </List.Item>
        )}
      />
    );
  }
}

export default MentionList;