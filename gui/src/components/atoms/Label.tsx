import React from 'react';

interface Props {
  htmlFor?: string;
}

export const Label: React.FC<Props> = ({ htmlFor, children }) => (
  <label {...{ htmlFor }}>{children}</label>
);
