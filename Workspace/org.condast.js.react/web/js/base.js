/** @jsx React.DOM */
var React = require( 'react' );
var ReactDOM = require( 'react-dom' );

var MyComponent = React.createClass({
    render: function(){
        return (
            alert('hoi');
            <h1>Hello, world!</h1>
        );
    }
});

React.render(
        <MyComponent/>,
        document.getElementById('container');
);