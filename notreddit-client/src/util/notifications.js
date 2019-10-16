import { notification } from 'antd';

export function infoNotification(message) {
  return notification.info({
    message: 'notreddit',
    description: message
  });
}

export function successNotification(message) {
  return notification.success({
    message: 'notreddit',
    description: message
  });
}

export function errorNotification(error, message) {
  return notification.error({
    message: 'notreddit',
    description: message || error.message || 'Sorry! Something went wrong. Please try again!'
  });
}