import React from 'react';
import styled from '@emotion/styled';

const FieldSetStyled = styled.fieldset(`
  margin: 0;
  padding: 0;
  border: none;
  & > .row {
    margin-top: 12px;
  }
`);

export const FieldSet: React.FC = ({ children }) => (
  <FieldSetStyled>{children}</FieldSetStyled>
);
