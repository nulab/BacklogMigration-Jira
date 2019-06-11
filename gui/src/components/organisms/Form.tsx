import React from 'react';
import { FieldSet } from '../atoms/FieldSet';
import { BasicForm } from '../molecules/BasicForm';

export const Form = () => (
  <form id='myForm' onSubmit={() => {}}>
    <FieldSet>
      <BasicForm />
      <div className='button-area'>
        <input type='submit' className='button' value='Execute' tabIndex={5} />
      </div>
    </FieldSet>
  </form>
);
