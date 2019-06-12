import React from 'react';
import styled from '@emotion/styled';
import { jsx } from '@emotion/core';
import { Row } from '../atoms/Row';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Option } from '../atoms/Option';

const BacklogUrlFieldStyled = styled.div(`
  display: flex;
  align-items: center;
`);

const SpanStyled = styled.span(`
  flex: auto 0 0;
  padding: 0 10px;
`);

export const BasicForm = () => (
  <>
    <Row>
      <Label htmlFor='backlogUrl'>Backlog URL</Label>
      <BacklogUrlFieldStyled>
        <SpanStyled>https://</SpanStyled>
        <Input
          id='backlogUrl'
          name='spaceId'
          value=''
          tabIndex={1}
          css={{ flex: 'auto 1 1' }}
        />
        <Select name='domain' tabIndex={2}>
          <Option value='.backlog.com' />
          <Option value='.backlog.jp' />
          <Option value='.backlogtool.com' />
        </Select>
      </BacklogUrlFieldStyled>
    </Row>
    <Row>
      <Label htmlFor='apiKey'>API Key</Label>
      <Input id='apiKey' name='apiKey' value='' tabIndex={3} />
    </Row>
    <Row>
      <Label htmlFor='projectKey'>Project Key</Label>
      <Input id='projectKey' name='projectKey' value='' tabIndex={4} />
    </Row>
  </>
);
