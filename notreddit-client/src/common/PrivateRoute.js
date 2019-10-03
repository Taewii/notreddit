import React from 'react';
import {
  Route,
  Redirect
} from "react-router-dom";


const PrivateRoute = ({ component: Component, authenticated, redirectPath, ...rest }) => (
  <Route
    {...rest}
    render={props =>
      authenticated ? (
        <Component {...rest} {...props} />
      ) : (
          <Redirect
            to={{
              pathname: redirectPath ? redirectPath : '/login',
              state: { from: props.location }
            }}
          />
        )
    }
  />
);

export default PrivateRoute