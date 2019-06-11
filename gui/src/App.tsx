import React from 'react';
import './App.css';
import { Label } from './components/atoms/Label';
import { Input } from './components/atoms/Input';
import { FieldSet } from './components/atoms/FieldSet';
import { Row } from './components/atoms/Row';
import { Option } from './components/atoms/Option';

const App: React.FC = () => {
  return (
    <div className='App'>
      <form id='myForm' onSubmit={() => {}}>
        <FieldSet>
          <Row>
            <Label htmlFor='backlogUrl'>Backlog URL</Label>
            <div className='space-id-field'>
              <span className='space-id-field__name'>https://</span>
              <Input
                id='backlogUrl'
                name='space'
                value=''
                tabIndex={1}
                className='space-id-field__id'
              />
              <select
                name='domain'
                tabIndex={2}
                className='space-id-field__domain'
              >
                <Option value='.backlog.com' />
                <Option value='.backlog.jp' />
                <Option value='.backlogtool.com' />
              </select>
            </div>
          </Row>
          <Row>
            <Label htmlFor='apiKey'>API Key</Label>
            <Input id='apiKey' name='apiKey' value='' tabIndex={3} />
          </Row>
          <Row>
            <Label htmlFor='projectKey'>Project Key</Label>
            <Input id='projectKey' name='projectKey' value='' tabIndex={4} />
          </Row>
          <div className='button-area'>
            <input
              type='submit'
              className='button'
              value='Execute'
              tabIndex={5}
            />
          </div>
        </FieldSet>
      </form>
    </div>
  );
};

export default App;
