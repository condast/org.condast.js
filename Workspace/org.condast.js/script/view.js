<script>
var contacts = [
	{key: 1, name: "James K Nelson", email: "james@jamesknelson.com", description: "Front-end Unicorn"},
	{key: 2, name: "Jim", email: "jim@example.com"},
	{key: 3, name: "Joe"}
	];

var ContactItem = React.createClass({
  propTypes: {
    name: React.PropTypes.string.isRequired,
  },

  render: function() {
    return (
      React.createElement('li', {className: 'Contact'},
        React.createElement('h2', {className: 'Contact-name'}, this.props.name)
      )
    )
  }
});
</script>