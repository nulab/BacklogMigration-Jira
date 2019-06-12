import React from 'react';
import styled from '@emotion/styled';

const InputStyled = styled.input(`
  outline: none;
  width: 100%;
  border: solid 1px #ccc;
  border-radius: 4px;
  padding: 8px 15px;
  font-size: 1rem;

  &:focus {
    border-color: #4d90fe;
  }
`);

interface Props {
  id: string;
  name: string;
  value: string;
  tabIndex: number;
  className?: string;
}

export const Input: React.FC<Props> = props => (
  <InputStyled type='text' {...props} />
);
