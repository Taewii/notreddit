import React, { Component } from 'react';
import './AllPosts.css';

import { List, Icon, notification } from 'antd';

const IconText = ({ type, text }) => (
  <span>
    <Icon type={type} style={{ marginRight: 8 }} />
    {text}
  </span>
);

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
        href: 'link to post',
        title: 'Title goes here',
        content: 'Lorem ipsum dolor, sit amet consectetur adipisicing elit. Facilis, soluta',
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
        renderItem={item => (
          <List.Item
            key={item.title}
            actions={[
              <IconText type="arrow-up" key="list-vertical-star-o" />,
              <span>123</span>,
              <IconText type="arrow-down" key="list-vertical-like-o" />,
              <IconText type="message" text="2" key="list-vertical-message" />,
              <div>@creator</div>
            ]}
          >
            <div className="post-counter-container">
              <span className="post-counter">1</span>
            </div>
            <div className="image">
              <img src="https://image.cnbcfm.com/api/v1/image/105992231-1561667465295gettyimages-521697453.jpeg?v=1561667497&w=678&h=381" alt="asd" width="172" />
            </div>
            <List.Item.Meta
              title={<a href={item.href}>{item.title}</a>}
            />
            {item.content}
          </List.Item>
        )}
      />
    );
  }
}

export default AllPosts;