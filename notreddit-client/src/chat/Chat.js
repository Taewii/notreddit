import React, { Component } from 'react';
import './Chat.css';
import { Stomp } from '@stomp/stompjs';
import { getAvatarColor } from '../util/APIUtils';

class Chat extends Component {
  constructor(props) {
    super(props);
    this.currentUser = this.props.currentUser;
    this.username = null;
    this.client = null;

    if (this.currentUser != null) {
      this.username = this.currentUser.username;
    }

    this.state = {
      connected: false,
      messages: []
    };

    this.onConnected = this.onConnected.bind(this);
    this.sendMessage = this.sendMessage.bind(this);
    this.onMessageReceived = this.onMessageReceived.bind(this);
  }

  connect() {
    this.client = Stomp.client('ws://localhost:8000/stomp/websocket');
    this.client.debug = () => { }; // disable console messages
    this.client.connect({}, this.onConnected, this.onError);
    this.setState({ connected: true });
  }

  componentDidMount() {
    this.connect();
  }

  componentDidUpdate() {
    this.scrollToBottom();
  }

  scrollToBottom() {
    this.el.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
    document.getElementById('chat-page').style.height = '640px';
  }

  onConnected() {
    this.client.subscribe('/topic/public', this.onMessageReceived);

    this.client.send("/app/chat.addUser",
      {},
      JSON.stringify({ sender: this.username, type: 'JOIN' })
    );
  }

  sendMessage(e) {
    e.preventDefault();
    const input = document.getElementById('message');
    let messageContent = input.value.trim();
    input.value = '';

    if (messageContent && this.client) {
      const chatMessage = {
        sender: this.username,
        content: messageContent,
        type: 'CHAT'
      };

      this.client.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    }
  }

  onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    this.setState({ messages: [...this.state.messages, message] });
  }

  onError(error) {
    console.log(error);
    const connElement = document.querySelector('.connecting');
    connElement.textContent = 'Could not connect to server. Please refresh this page to try again!';
    connElement.style.color = 'red';
  }

  render() {
    const { connected, messages } = this.state;

    return (
      <div id="chat-page">
        <div className="chat-container">
          <div className="chat-header">
            <h2>Chat</h2>
          </div>
          <div className={`connecting ${connected ? 'hidden' : ''}`}>
            Connecting...
            </div>
          <ul id="messageArea">
            {messages.map((props, i) => <Message messageProps={props} key={i} />)}
            <div ref={el => { this.el = el; }} />
          </ul>
          <form id="messageForm" name="messageForm">
            <div className="form-group">
              <div className="input-group clearfix">
                <input
                  type="text"
                  id="message"
                  placeholder="Type a message..."
                  autoComplete="off"
                  className="form-control" />
                <button onClick={(e) => this.sendMessage(e)} type="submit" className="btn primary">Send</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    )
  }
};

const Message = ({ messageProps }) => {
  const sender = messageProps.sender;
  const content = messageProps.content;
  const type = messageProps.type;

  if (type !== 'CHAT') {
    return (
      <li className="event-message">
        <p>{sender} {type === 'JOIN' ? 'joined' : 'left'}!</p>
      </li>
    );
  }

  return (
    <li className="chat-message">
      <i style={{ backgroundColor: getAvatarColor(sender) }}>{sender[0]}</i>
      <span>
        <a href={'/user/' + sender}>{sender}</a>
      </span>
      <p>{content}</p>
    </li>
  );
}

export default Chat;