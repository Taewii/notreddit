import React, { Component } from 'react';
import './AllPosts.css';

import { List, Icon, notification } from 'antd';

class AllPosts extends Component {
  constructor(props) {
    super(props)
    this.state = {
      initLoading: true,
      loading: false,
      data: []
    };

    for (let i = 0; i < 23; i++) {
      this.state.data.push({
        href: 'https://i.redd.it/oadss15xhqo31.jpg',
        title: 'Title goes here',
        creator: 'creator?',
        subreddit: 'subreddit?',
        points: '1233'
      });
    }
  }

  // componentDidMount() {
  //   fetch()
  //     .then(res => {
  //       this.setState({
  //         initLoading: false,
  //         data: res.users,
  //       });
  //     }).catch(error => {
  //       this.props.history.push('/');
  //       notification.error({
  //         message: 'notreddit',
  //         description: error.message || 'Sorry! Something went wrong. Please try again!'
  //       });
  //     });
  // }

  upvote(event) {
    console.log('upvote')
  }

  downvote(event) {
    console.log('downvote')
  }

  render() {
    const { data } = this.state;

    return (
      <List
        bordered
        itemLayout="vertical"
        size="large"
        pagination={{
          onChange: page => {
            console.log(page);
          },
          pageSize: 5,
        }}
        dataSource={data}
        renderItem={post => (
          <List.Item>
            <div className="grid-container">
              <div className="id">1</div>
              <div className="votes">
                <p className="upvote" onClick={this.upvote}>
                  <Icon type="caret-up" />
                </p>
                <p className="score">
                  {post.points}
                </p>
                <p className="downvote" onClick={this.downvote}>
                  <Icon type="caret-down" />
                </p>
              </div>
              <div className="picture">
                <a href="#">
                  <img className="pic" src={post.href} alt="pic" />
                </a>
              </div>
              <div className="title">
                <a href="#">{post.title}</a>
              </div>
              <div className="description">
                submitted 13 hours ago by <a href="#">{post.creator}</a> to <a href="#">{post.subreddit}</a>
              </div>
              <div className="actions">
                <Icon type="message" /> <a href="#">609 comments</a>
              </div>
            </div>
          </List.Item>
        )}
      />
    );
  }
}

export default AllPosts;