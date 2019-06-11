import React from 'react';
import './App.css';
import { Label } from './components/atoms/Label';
import { Input } from './components/atoms/Input';

const App: React.FC = () => {
  return (
    <div className='App'>
      <form id='myForm' onSubmit={() => {}}>
        <fieldset>
          <div className='row'>
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
                <option value='.backlog.com'>.backlog.com</option>
                <option value='.backlog.jp'>.backlog.jp</option>
                <option value='.backlogtool.com'>.backlogtool.com</option>
              </select>
            </div>
          </div>
          <div className='row'>
            <Label htmlFor='apiKey'>API Key</Label>
            <Input id='apiKey' name='apiKey' value='' tabIndex={3} />
          </div>
          <div className='row'>
            <Label htmlFor='projectKey'>Project Key</Label>
            <Input id='projectKey' name='projectKey' value='' tabIndex={4} />
          </div>
          <div className='button-area'>
            <input
              type='submit'
              className='button'
              value='Execute'
              tabIndex={5}
            />
          </div>
        </fieldset>
      </form>
    </div>
  );
};

export default App;
