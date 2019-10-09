import { ACCESS_TOKEN } from './constants';

export const request = (options) => {
  const headers = new Headers({
    'Content-Type': 'application/json',
  })

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

export const timeSince = (time) => {
  switch (typeof time) {
    case 'number':
      time = +new Date(time * 1000) // x1000 cus js Date expects milliseconds but java returns seconds
      break;
    case 'string':
      time = +new Date(time);
      break;
    case 'object':
      if (time.constructor === Date) time = time.getTime();
      break;
    default:
      time = +new Date();
  }

  const timeFormats = [
    [60, 'seconds ago', 1], // 60
    [120, '1 minute ago', '1 minute from now'], // 60*2
    [3600, 'minutes ago', 60], // 60*60, 60
    [7200, '1 hour ago', '1 hour from now'], // 60*60*2
    [86400, 'hours ago', 3600], // 60*60*24, 60*60
    [172800, 'yesterday', 'Tomorrow'], // 60*60*24*2
    [604800, 'days ago', 86400], // 60*60*24*7, 60*60*24
    [1209600, 'last week', 'Next week'], // 60*60*24*7*4*2
    [2419200, 'weeks ago', 604800], // 60*60*24*7*4, 60*60*24*7
    [4838400, 'last month', 'Next month'], // 60*60*24*7*4*2
    [29030400, 'months ago', 2419200], // 60*60*24*7*4*12, 60*60*24*7*4
    [58060800, 'last year', 'Next year'], // 60*60*24*7*4*12*2
    [2903040000, 'years ago', 29030400], // 60*60*24*7*4*12*100, 60*60*24*7*4*12
    [5806080000, 'last century', 'Next century'], // 60*60*24*7*4*12*100*2
    [58060800000, 'centuries ago', 2903040000] // 60*60*24*7*4*12*100*20, 60*60*24*7*4*12*100
  ];

  let seconds = (+new Date() - time) / 1000;
  let token = 'ago';
  let list_choice = 1;

  if (seconds === 0) {
    return 'Just now'
  }

  if (seconds < 0) {
    seconds = Math.abs(seconds);
    token = 'from now';
    list_choice = 2;
  }

  let i = 0;
  let format = 0;

  // eslint-disable-next-line no-cond-assign
  while (format = timeFormats[i++])
    if (seconds < format[0]) {
      if (typeof format[2] == 'string')
        return format[list_choice];
      else
        return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
    }
  return time;
}