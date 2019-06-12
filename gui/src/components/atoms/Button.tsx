import React from 'react';
import styled from '@emotion/styled';

const ButtonStyled = styled.button(`
  outline: none;
  min-width: 120px;
  margin: 0 auto;
  color: white;
  background-color: #4285f4;
  font-size: 1rem;
  font-weight: 500;
  line-height: 1.3;
  display: inline-block;
  padding: 13px 20px;
  cursor: pointer;
  text-align: center;
  text-decoration: none;
  border: 0 none;
  border-radius: 6px;
  transition: background-color 0.1s 0s linear, border-color 0.1s 0s linear;
  border: 2px solid #4285f4;

  &:hover {
    background-color: #2569d4;
    border-color: #2569d4;
  }
  
  &:focus {
    border-color: #0949b5;
  }
`);

interface Props {
  value: string;
  tabIndex: number;
}

export const Button: React.FC<Props> = ({ value, tabIndex }) => (
  <ButtonStyled type='button' tabIndex={tabIndex}>
    {value}
  </ButtonStyled>
);
