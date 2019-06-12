import React from 'react';
import styled from '@emotion/styled';
import { FieldSet } from '../atoms/FieldSet';
import { BasicForm } from '../molecules/BasicForm';
import { Button } from '../atoms/Button';

const ButtonArea = styled.div(`
  margin-top: 20px;
  text-align: center;
`);

export const Form = () => (
  <form id='myForm' onSubmit={() => {}}>
    <FieldSet>
      <BasicForm />
      <ButtonArea>
        <Button value='Execute' tabIndex={5} />
      </ButtonArea>
    </FieldSet>
  </form>
);
