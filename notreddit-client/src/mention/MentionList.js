import React, { Component } from 'react';
import './MentionList.css';

import { List, Icon, Tooltip } from 'antd';

import { errorNotification } from '../util/notifications';
import { timeSince } from '../util/APIUtils';
import { getUsersMentions } from '../services/mentionService';

const IconText = ({ type, text }) => (
  <span>
    <Icon type={type} style={{ marginRight: 8 }} />
    {text}
  </span>
);


class MentionList extends Component {
  constructor(props) {
    super(props)
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
            mentions: res,
            // totalComments: res.total,
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
    console.log(mentionId)
  }

  markAsUnread(mentionId) {
    console.log(mentionId)
  }

  render() {
    const { mentions, page, pageSize } = this.state;

    return (
      <List
        bordered
        itemLayout="vertical"
        size="small"
        // pagination={{
        //   showSizeChanger: true,
        //   total: totalComments,
        //   defaultCurrent: 1,
        //   current: page,
        //   pageSize: pageSize,
        //   onChange: (page, pageSize) => {
        //     this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
        //   },
        //   onShowSizeChange: (page, pageSize) => {
        //     this.props.history.push(`${window.location.pathname}?page=${page}&pageSize=${pageSize}`);
        //   }
        // }}
        dataSource={mentions}
        renderItem={mention => (
          <List.Item
            className="comment-item"
            key={mention.id}
            actions={[
              <span key="comment-basic-upvote" onClick={() => this.markAsRead(mention.id)}>
                <Tooltip title="Mark as read">
                  <IconText type="check" text="Mark as read" />
                </Tooltip>
              </span>,
              <span key="comment-basic-upvote" onClick={() => this.markAsUnread(mention.id)}>
                <Tooltip title="Mark as unread">
                  <IconText type="close" text="Mark as unread" />
                </Tooltip>
              </span>
            ]}
          >
            <List.Item.Meta
              title={<span><span className="mention-type">comment reply </span> <a href={'/post/' + mention.commentPostId}>{mention.commentPostTitle}</a></span>}
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