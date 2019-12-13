import { ACCESS_TOKEN } from './constants';

const request = (method, url, options = {}) => {
  const headers = new Headers({
    'Content-Type': 'application/json'
  });

  if (localStorage.getItem(ACCESS_TOKEN)) {
    headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN));
  }

  const defaults = { headers: headers };
  options = Object.assign({}, defaults, options);
  options.method = method;

  return fetch(url, options)
    .then(response =>
      response.json().then(json => {
        if (!response.ok) {
          return Promise.reject(json);
        }
        return json;
      })
    );
};

export const requestMultipart = (options) => {
  const headers = new Headers();

  if (localStorage.getItem(ACCESS_TOKEN)) {
    headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
  }

  const defaults = { headers: headers };
  options = Object.assign({}, defaults, options);

  return fetch(options.url, options)
    .then(response =>
      response.json().then(json => {
        if (!response.ok) {
          return Promise.reject(json);
        }
        return json;
      })
    );
};

export const get = function (url) {
  return request('GET', url);
};

export const post = function (url, data) {
  return request('POST', url, data);
};

export const put = function (url, data) {
  return request('PUT', url, data);
};

export const patch = function (url, data) {
  return request('PATCH', url, data);
};

export const remove = function (url, data) {
  return request('DELETE', url, data);
};