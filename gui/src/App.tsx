import React from 'react';
import { injectGlobal } from 'emotion';
import { Form } from './components/organisms/Form';

injectGlobal`
* {
  box-sizing: border-box;
}
body {
  margin: 20px;
  font-family: sans-serif;
}
`;

const App: React.FC = () => {
  return <Form />;
};

export default App;
