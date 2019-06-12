import React from 'react';
import { injectGlobal } from 'emotion';
import { ParameterForm } from './components/organisms/ParameterForm';

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
  return <ParameterForm />;
};

export default App;
